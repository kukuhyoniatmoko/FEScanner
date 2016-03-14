#
#           Circle CI & gradle.properties live in harmony
#
# Android convention is to store your API keys in a local, non-versioned
# gradle.properties file. Circle CI doesn't allow users to upload pre-populated
# gradle.properties files to store this secret information, but instaed allows
# users to store such information as environment variables.
#
# This script creates a local gradle.properties file on current the Circle CI
# instance. It then reads environment variable TEST_API_KEY_ENV_VAR which a user
# has defined in their Circle CI project settings environment variables, and
# writes this value to the Circle CI instance's gradle.properties file.
#
# You must execute this script via your circle.yml as a pre-process dependency,
# so your gradle build process has access to all variables.
#
#   dependencies:
#       pre:
#        - source environmentSetup.sh && copyEnvVarsToGradleProperties

#!/usr/bin/env bash

function copyEnvVarsToGradleProperties {
    SECRET_PROPERTIES=$HOME"/.gradle/secret.properties"
    export SECRET_PROPERTIES
    echo "Secret Properties should exist at $SECRET_PROPERTIES"

    if [ ! -f "$SECRET_PROPERTIES" ]; then
        echo "Gradle Properties does not exist"

        echo "Creating Gradle Properties file..."
        touch $SECRET_PROPERTIES

        echo "Writing KEY to secret.properties..."
        echo "TWITTER_KEY=$TWITTER_KEY" >> $SECRET_PROPERTIES
        echo "TWITTER_SECRET=$TWITTER_SECRET" >> $SECRET_PROPERTIES
        echo "FABRIC_KEY=$FABRIC_KEY" >> $SECRET_PROPERTIES
        echo "FACEBOOK_KEY=$FACEBOOK_KEY" >> $SECRET_PROPERTIES
        echo "PLAY_SERVICE_GEO_KEY=$PLAY_SERVICE_GEO_KEY" >> $SECRET_PROPERTIES
        echo "PLAY_SERVICE_BROWSER_KEY=$PLAY_SERVICE_BROWSER_KEY" >> $SECRET_PROPERTIES
    fi
}