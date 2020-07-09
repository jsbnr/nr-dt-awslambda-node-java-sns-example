## AWS Lambda Distributed Tracing - Backend Application

This is a tiny Ruby app that simply responds to http requests and is instrumented with New Relic. It can be used to act as an example backend for a distrtibuted trace demo. You should host this app in a publicy accessible place. Heroku for example.

Be sure to add your New Relic license key to the newrelic.yml file.

Run locally with: `ruby app.rb`

If you have setup heroku push the app to heroku with: `git push heroku master` (you might need this app in its own repo for that to work)