package com.twosixlabs.dart.nlp.odinson

import ai.lum.odinson.DataGatherer.VerboseLevels
import ai.lum.odinson.Mention
import better.files.File
import com.twosixlabs.dart.nlp.odinson.config.OdinsonConfig
import org.scalatest.BeforeAndAfterAll

import java.util.concurrent.TimeUnit.SECONDS
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class OdinsonExtractorTest extends OdinsonTestBase with BeforeAndAfterAll {
    // use a different directory for the extractor test...
    private val extractorConf : OdinsonConfig = ODINSON_CONF.copy( index = ODINSON_CONF.index.copy( dir = File( "target/extractor_test" ) ) )

    override def beforeAll( ) : Unit = {
        val indexer = new OdinsonIndexer( extractorConf )
        Await.result( indexer.indexDocuments( ODINSON_DOCS ), Duration( 10, SECONDS ) )
        indexer.close()
    }

    override def afterAll( ) : Unit = extractorConf.index.dir.delete()

    "Odinson Extractor" should "execute the basic ruleset containing one rule file" in {
        val extractor = new OdinsonExtractor( odinsonConfig = extractorConf )
        val query = ExtractorQuery( ruleset = "basic" )
        val actualNouns =
            extractor.execute( query )
              .toList
              .flatMap( m => {
                  m.populateFields( VerboseLevels.All )
                  extractArgValue( m, "result" )
              } )

        val expectedNouns = Seq( "Michael", "Lightning", "Bolt", "band", "time" )
        actualNouns shouldBe expectedNouns
    }

    "Odinson Extractor" should "execute the examples ruleset containing multiple rule files" in {
        val extractor = new OdinsonExtractor( odinsonConfig = extractorConf )
        val query = ExtractorQuery( ruleset = "examples" )

        val results = extractor.execute( query ).toList

        val nouns = results.flatMap( m => {
            m.populateFields( VerboseLevels.All )
            extractArgValue( m, "noun" )
        } )
        val verbs = results.flatMap( m => {
            m.populateFields( VerboseLevels.All )
            extractArgValue( m, "verb" )
        } )

        val expectedNouns = List( "Michael", "Lightning", "Bolt", "band", "time" ).sorted
        val expectedVerbs = List( "is", "is", "do", "like", "are" ).sorted

        nouns.sorted shouldBe expectedNouns
        verbs.sorted shouldBe expectedVerbs
    }

    "Odinson Extractor" should "execute the a ruleset that includes query variables" in {
        val extractor = new OdinsonExtractor( odinsonConfig = extractorConf )
        val query = ExtractorQuery( ruleset = "queries", variables = Map( "query" -> "Michael" ), queryType = NORM )

        val result =
            extractor
              .execute( query )
              .toList
              .flatMap( m => {
                  m.populateFields( VerboseLevels.All )
                  extractArgValue( m, "result" )
              } )

        result should contain( "Michael" )
    }

    "OdinsonExtractor" should "only load the rules in a single file" in {
        val extractor = new OdinsonExtractor( odinsonConfig = extractorConf )
        val query = ExtractorQuery( "examples", Some( "get-nouns.yml" ) )

        val matchedRules =
            extractor
              .execute( query )
              .map( _.foundBy )
              .toSet

        matchedRules.size shouldBe 1
        matchedRules shouldBe Set( "match-all-nouns" )
    }

    private def extractArgValue( mention : Mention, key : String ) : Option[ String ] = {
        mention.arguments.get( key ) match {
            case Some( resultMatch ) => {
                if ( resultMatch.isEmpty ) None
                else resultMatch.head.mentionFields.get( "raw" ) match {
                    case Some( lemmas ) => lemmas.headOption
                    case None => None
                }
            }
            case None => None
        }
    }

}
