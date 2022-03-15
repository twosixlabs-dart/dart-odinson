package com.twosixlabs.dart.nlp.odinson

import ai.lum.odinson.digraph.Vocabulary
import ai.lum.odinson.utils.IndexSettings
import ai.lum.odinson.{OdinsonIndexWriter, Document => OdinsonDocument}
import com.twosixlabs.dart.nlp.odinson.config.OdinsonConfig
import org.apache.lucene.document.{Document => LuceneDocument}
import org.apache.lucene.store.{Directory, FSDirectory}
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}


case class IndexerResult( corpusSize : Int, numBlocks : Int )

class OdinsonIndexer( config : OdinsonConfig ) {

    private val LOG : Logger = LoggerFactory.getLogger( getClass )

    implicit private val ec : ExecutionContext = ExecutionContext.global

    private val luceneDir : Directory = FSDirectory.open( config.index.dir.path )

    // TODO - @michael - find out what `dependencies.txt` is from the original repo
    private val vocab : Vocabulary = Vocabulary.empty

    private val indexWriter : OdinsonIndexWriter = {
        new OdinsonIndexWriter( directory = luceneDir,
                                vocabulary = vocab,
                                normalizedTokenField = config.fields.normalizedTokenField,
                                addToNormalizedField = config.fields.addToNormalizedField.toSet,
                                incomingTokenField = config.fields.incomingTokenField,
                                outgoingTokenField = config.fields.outgoingTokenField,
                                maxNumberOfTokensPerSentence = config.index.maxNumberOfTokensPerSentence,
                                invalidCharacterReplacement = config.index.invalidCharacterReplacement,
                                settings = IndexSettings( config.fields.storedFields :+ config.fields.displayField ),
                                displayField = config.fields.displayField )
    }

    def indexDocuments( docs : Seq[ OdinsonDocument ] ) : Future[ IndexerResult ] = {
        val sourceDocCount : Int = docs.size
        val indexingOp : Seq[ Future[ Int ] ] = docs.map( indexDocument )

        Future.sequence( indexingOp ) transform {
            case Success( value ) => Success( IndexerResult( corpusSize = sourceDocCount, numBlocks = value.sum ) )
            case Failure( e ) => Failure( e )
        }
    }

    def indexDocument( doc : OdinsonDocument ) : Future[ Int ] = {
        Future {
            val block = convertToLucene( doc )
            indexWriter.addDocuments( block )
            block.size
        }
    }

    private def convertToLucene( doc : OdinsonDocument ) : Seq[ LuceneDocument ] = indexWriter.mkDocumentBlock( doc )

    def close( ) : Unit = indexWriter.close()

}

object OdinsonIndexer {
    private lazy val LOG : Logger = LoggerFactory.getLogger( getClass )

    def setupIndexDir( config : OdinsonConfig ) : Try[ Unit ] = {
        Try {
            if ( !config.index.dir.exists ) config.index.dir.createDirectories()
            else {
                if ( !config.index.dir.isEmpty ) {
                    LOG.warn( s"${config.index.dir.canonicalPath} is not empty, the data in this directory will be lost..." )
                    config.index.dir.delete()
                    config.index.dir.createDirectories()
                }
            }
        }
    }
}
