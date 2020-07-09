## AWS Lambda Distributed Tracing - Frontend Application

This is a tiny Ruby app that acts as the public customer entry point to an example distributed trace demo application. You should host this app in a publicy accessible place. Heroku for example.

Be sure to add your New Relic license key to the newrelic.yml file and to configure the `backendDomain` in app.rb

Run locally with: `ruby app.rb`

If you have setup heroku push the app to heroku with: `git push heroku master` (you might need this app in its own repo for that to work)