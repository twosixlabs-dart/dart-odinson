package com.twosixlabs.dart.nlp.odinson

import better.files.File
import com.twosixlabs.dart.nlp.odinson.config.OdinsonConfig
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.store.FSDirectory
import org.scalatest.BeforeAndAfterEach

import java.util.concurrent.TimeUnit.MINUTES
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class OdinsonIndexerTest extends OdinsonTestBase with BeforeAndAfterEach {

    // use a different directory for the indexer test...
    private val indexerConf : OdinsonConfig = ODINSON_CONF.copy( index = ODINSON_CONF.index.copy( dir = File( "target/indexer_test" ) ) )

    override def afterEach( ) : Unit = indexerConf.index.dir.delete( swallowIOExceptions = true )

    "Odinson Indexer" should "set up an empty index" in {
        OdinsonIndexer.setupIndexDir( indexerConf )

        indexerConf.index.dir.exists shouldBe true
        indexerConf.index.dir.isEmpty shouldBe true
    }

    "Odinson Indexer" should "overwrite an existing index" in {
        OdinsonIndexer.setupIndexDir( indexerConf )
        val touch = indexerConf.index.dir.createChild( "test" )
        indexerConf.index.dir.list.toList should contain( touch )

        OdinsonIndexer.setupIndexDir( indexerConf )

        indexerConf.index.dir.exists shouldBe true
        indexerConf.index.dir.isEmpty shouldBe true
    }

    "Odinson Indexer" should "index some documents" in {
        val indexer = new OdinsonIndexer( indexerConf )

        val indexerOp = indexer.indexDocuments( ODINSON_DOCS )
        val indexerResults : IndexerResult = Await.result( indexerOp, Duration( 2, MINUTES ) )
        indexer.close()

        val index = getLuceneReader( indexerConf.index.dir )
        index.numDocs() shouldBe 5

        indexerResults.corpusSize shouldBe ODINSON_DOCS.size
        indexerResults.numBlocks shouldBe index.numDocs()
    }

    private def getLuceneReader( index : File ) : DirectoryReader = {
        DirectoryReader.open( FSDirectory.open( index.path ) )
    }

}
