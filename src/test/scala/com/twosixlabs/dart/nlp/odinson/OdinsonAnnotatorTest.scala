package com.twosixlabs.dart.nlp.odinson

import ai.lum.odinson.{Document => OdinsonDocument}
import com.twosixlabs.dart.utils.AsyncDecorators.DecoratedFuture
import org.scalatest.BeforeAndAfterAll

import scala.util.{Failure, Success}


class OdinsonAnnotatorTest extends OdinsonTestBase with BeforeAndAfterAll {

    private val SOURCE_DOC : String = "Don't be a hater. Michael is the best."
    private val VALIDATION_DOC : String = """{"id":"123abc","metadata":[],"sentences":[{"numTokens":6,"fields":[{"$type":"ai.lum.odinson.TokensField","name":"raw","tokens":["Do","n't","be","a","hater","."]},{"$type":"ai.lum.odinson.TokensField","name":"word","tokens":["Do","not","be","a","hater","."]},{"$type":"ai.lum.odinson.TokensField","name":"tag","tokens":["VB","RB","VB","DT","NN","."]},{"$type":"ai.lum.odinson.TokensField","name":"lemma","tokens":["do","not","be","a","hater","."]},{"$type":"ai.lum.odinson.TokensField","name":"entity","tokens":["O","O","O","O","O","O"]},{"$type":"ai.lum.odinson.TokensField","name":"chunk","tokens":["B-VP","I-VP","I-VP","B-NP","I-NP","O"]},{"$type":"ai.lum.odinson.GraphField","name":"dependencies","edges":[[4,0,"aux"],[4,1,"neg"],[4,2,"cop"],[4,3,"det"],[4,5,"punct"]],"roots":[4]}]},{"numTokens":5,"fields":[{"$type":"ai.lum.odinson.TokensField","name":"raw","tokens":["Michael","is","the","best","."]},{"$type":"ai.lum.odinson.TokensField","name":"word","tokens":["Michael","is","the","best","."]},{"$type":"ai.lum.odinson.TokensField","name":"tag","tokens":["NNP","VBZ","DT","JJS","."]},{"$type":"ai.lum.odinson.TokensField","name":"lemma","tokens":["Michael","be","the","best","."]},{"$type":"ai.lum.odinson.TokensField","name":"entity","tokens":["PERSON","O","O","O","O"]},{"$type":"ai.lum.odinson.TokensField","name":"chunk","tokens":["B-NP","B-VP","B-NP","I-NP","O"]},{"$type":"ai.lum.odinson.GraphField","name":"dependencies","edges":[[3,2,"det"],[3,4,"punct"],[3,0,"nsubj"],[3,1,"cop"]],"roots":[3]}]}]}"""

    override def beforeAll( ) : Unit = Odinson.initDynet()

    "Odinson Annotator" should "annotate a document" in {
        val annotator : OdinsonAnnotator = new OdinsonAnnotator( ODINSON_CONF )
        annotator.warmup()

        val docId = "123abc"
        val text = SOURCE_DOC

        annotator.annotateDocument( docId, text ).synchronously( timeoutMs = 200000 ) match {
            case Success( Some( odinsonDoc ) ) => {
                val expected : OdinsonDocument = OdinsonDocument.fromJson( VALIDATION_DOC )
                expected shouldBe odinsonDoc
            }
            case Success( None ) => fail( "this test document should not cause this situation to happen..." )
            case Failure( e ) => {
                e.printStackTrace()
                fail( s"failed to annotate document" )
            }
        }
    }

}
