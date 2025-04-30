#! /usr/bin/env ruby

STDIN.sync = true
STDOUT.sync = true

if ARGV.length > 0
  file_path = ARGV[0]
  begin
    File.open(file_path) do |file|
      file.each_line do |line|
        STDOUT.puts line
      end
    end
  rescue Errno::ENOENT
    raise "File not found: #{file_path}"
  end
end

while line = STDIN.gets()
  STDOUT.puts(line)
end
