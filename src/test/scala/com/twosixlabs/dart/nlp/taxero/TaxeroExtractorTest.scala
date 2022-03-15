package com.twosixlabs.dart.nlp.taxero


import better.files.File
import com.twosixlabs.dart.nlp.odinson.{OdinsonAnnotator, OdinsonIndexer, OdinsonTestBase}
import com.twosixlabs.dart.utils.AsyncDecorators.DecoratedFuture
import org.scalatest.BeforeAndAfterAll

import java.util.UUID
import scala.util.{Failure, Success}

class TaxeroExtractorTest extends OdinsonTestBase with BeforeAndAfterAll {

    private val testIndex : File = File( "target/taxero_test_index" )

    private val testConfig : TaxeroConfig = {
        TAXERO_CONFIG.copy( odinsonConfig = TAXERO_CONFIG.odinsonConfig.copy( index = TAXERO_CONFIG.odinsonConfig.index.copy( dir = testIndex ) ) )
    }

    override def beforeAll( ) : Unit = {
        val annotator = new OdinsonAnnotator( testConfig.odinsonConfig )
        val annotated = List( "Taxero is a scala project", "Taxero is software", "Taxero is open source", "The source code is available on GitHub" )
          .map( x => {
              val id = UUID.randomUUID().toString()
              annotator.annotateDocument( id, x ).synchronously( timeoutMs = 200000 ) match {
                  case Success( Some( odinsonDoc ) ) => odinsonDoc
                  case Success( None ) => fail( "this test document should not cause this situation to happen..." )
                  case Failure( e ) => {
                      e.printStackTrace()
                      fail( s"failed to annotate document" )
                  }
              }
          } )

        val indexer = new OdinsonIndexer( testConfig.odinsonConfig.copy( index = testConfig.odinsonConfig.index.copy( dir = testIndex ) ) )
        annotated.map( x => indexer.indexDocument( x ).synchronously() )
        indexer.close()
    }

    override def afterAll( ) : Unit = {
        if ( testIndex.exists ) testIndex.delete( swallowIOExceptions = true )
    }

    "Taxero Extractor" should "extract hypernyms for a query" in {
        val taxero = new TaxeroExtractor( testConfig )
        val results = taxero.searchHypernyms( "Taxero" )

        results.size shouldBe 3
        results should contain( Hypernym( "taxero", "scala project", BigDecimal( 0.34664290499802863 ) ) )
        results should contain( Hypernym( "taxero", "open source", BigDecimal( 0.34664290499802863 ) ) )
        results should contain( Hypernym( "taxero", "software", BigDecimal( 0.34664290499802863 ) ) )
    }

    "Taxero Extractor" should "extract hyponyms for a query" in {
        val taxero = new TaxeroExtractor( testConfig )
        val results = taxero.searchHyponyms( "software" )

        results.size shouldBe 1
        results should contain( Hyponym( "software", "Taxero", BigDecimal( 0.34664290499802863 ) ) )
    }


}
