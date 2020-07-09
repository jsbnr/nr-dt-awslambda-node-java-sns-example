# myapp.rb
require 'rubygems'
require 'json'
require 'net/http'
require 'sinatra'
require 'newrelic_rpm'

JSON_HEADER = {'Content-Type' => 'application/json; charset=utf-8'}

get '/' do
    # puts String((100..800).to_a.sample)
    delay = rand(1..10).to_f/10
    puts String(delay)
    sleep delay
     
    'This is the backend sending a response (with delay ' + String(delay) + ' seconds)'
end

