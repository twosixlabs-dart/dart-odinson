package com.twosixlabs.dart.nlp.taxero

import org.clulab.taxero.TaxonomyReader

sealed trait TaxonomyMatch {
    val query : String
    val result : String
    val score : BigDecimal
}

case class Hyponym( query : String, result : String, score : BigDecimal ) extends TaxonomyMatch

case class Hypernym( query : String, result : String, score : BigDecimal ) extends TaxonomyMatch

class TaxeroExtractor( config : TaxeroConfig ) {

    val taxonomyReader = TaxonomyReader.fromConfig( config.configShim() )

    def searchHypernyms( text : String ) : Seq[ TaxonomyMatch ] = {
        taxonomyReader
          .getRankedHypernyms( List( text ), config.lemmatize )
          .map( result => {
              Hypernym( result.query.mkString( " " ), result.result.mkString( " " ), BigDecimal( result.score ) )
          } )
    }

    def searchHyponyms( text : String ) : Seq[ TaxonomyMatch ] = {
        taxonomyReader
          .getRankedHyponyms( List( text ), config.lemmatize )
          .map( result => {
              Hyponym( result.query.mkString( " " ), result.result.mkString( " " ), BigDecimal( result.score ) )
          } )
    }

}
