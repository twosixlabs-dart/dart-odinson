package com.twosixlabs.dart.nlp.taxero

import better.files.File
import com.twosixlabs.dart.nlp.odinson.config.OdinsonConfig
import com.typesafe.config.{Config, ConfigFactory}

object TaxeroConfig {
    def fromConfig( config : Config ) : TaxeroConfig = {
        val wordEmbeddings : File = File( config.getString( "taxero.word.embeddings" ) )
        val lemmatize = config.getBoolean( "taxero.lemmatize" )
        val odinsonConf = OdinsonConfig.fromConfig( config.getConfig( "odinson" ) )
        TaxeroConfig( wordEmbeddings, lemmatize, odinsonConf )
    }
}

case class TaxeroConfig( embeddings : File, lemmatize : Boolean, odinsonConfig : OdinsonConfig ) {

    def configShim( ) : Config = {
        val shim =
            s"""
               | taxero {
               |     wordEmbeddings: ${embeddings.pathAsString}
               |     lemmatize: ${lemmatize}
               | }
               |""".stripMargin

        // merge config
        ConfigFactory.parseString( shim ).withFallback( odinsonConfig.configShim() )
    }

}
