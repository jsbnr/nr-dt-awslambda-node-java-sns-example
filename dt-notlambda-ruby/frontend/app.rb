# myapp.rb
require 'rubygems'
require 'json'
require 'net/http'
require 'sinatra'
require 'newrelic_rpm'

JSON_HEADER = {'Content-Type' => 'application/json; charset=utf-8'}

if !(ENV['BACKEND_DOMAIN']) 
    backendDomain = "https://xxxx.execute-api.eu-west-1.amazonaws.com/foo/bar" # The backend goes here, perhaps an AWS lambda app fronted by API gateway?
else 
    backendDomain = ENV['BACKEND_DOMAIN'] # can be provided as an environment variable
end



puts "Backend domain="+String(backendDomain)

get '/' do

    request.body.rewind
    uri = URI(String(backendDomain))

    req = Net::HTTP::Get.new(uri)
    puts "req" + String(uri.hostname)

    resp = Net::HTTP.start(uri.hostname, uri.port,:use_ssl => true, :verify_mode => OpenSSL::SSL::VERIFY_NONE) {|http|
      http.request(req)
    }
    puts "Response data: " + String(resp.body)
    [200, JSON_HEADER, String(resp.body).to_json]
end