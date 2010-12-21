#!/usr/bin/ruby

Messages = {}
for ending in ['en','de','fr','h4'] 
	File.open("qa/conf/messages."+ending) do |f| 
		Messages[ending]= f.read\
			.scan(/(.*?)=/)\
			.flatten
	end
end
All = Messages.values.inject([]) { |left,right| left|right }
for ending,vals in Messages
	missing = All-vals
	if !missing.empty?
		puts "Missing in #{ending}:"
		missing.each {|e| puts "\t#{e}\n"}
	end
end
