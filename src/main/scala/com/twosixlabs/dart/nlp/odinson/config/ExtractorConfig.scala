package com.twosixlabs.dart.nlp.odinson.config

import better.files.File
import com.typesafe.config.Config

object ExtractorConfig {
    def fromConfig( config : Config ) : ExtractorConfig = {
        val rules = File( config.getString( "rulesHome" ) )
        val state = config.getString( "state" )
        val lemmatize = config.getBoolean( "lemmatize" )
        ExtractorConfig( rules, state, lemmatize )
    }
}

case class ExtractorConfig( rulesHome : File, state : String, lemmatize : Boolean )