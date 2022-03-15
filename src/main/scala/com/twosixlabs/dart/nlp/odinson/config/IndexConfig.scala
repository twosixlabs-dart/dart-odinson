package com.twosixlabs.dart.nlp.odinson.config

import better.files.File
import com.typesafe.config.Config

object IndexConfig {

    def fromConfig( config : Config ) : IndexConfig = {
        val indexDir : File = File( config.getString( "dir" ) )
        val maxTokens : Int = config.getInt( "maxNumberOfTokensPerSentence" )
        val invalidCharReplacement : String = config.getString( "invalidCharacterReplacement" )

        IndexConfig( dir = indexDir,
                     maxNumberOfTokensPerSentence = maxTokens,
                     invalidCharacterReplacement = invalidCharReplacement )
    }
}

case class IndexConfig( dir : File,
                        maxNumberOfTokensPerSentence : Int,
                        invalidCharacterReplacement : String )
