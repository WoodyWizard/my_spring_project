from http.server import BaseHTTPRequestHandler, HTTPServer
import json

class RequestHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        # Data to be sent as json
        data = {
            'message': 'Hello, World! This is a simple HTTP server response in JSON.'
        }

        # Send response status code
        self.send_response(200)

        # Send headers
        self.send_header('Content-type', 'application/json')
        self.end_headers()

        # Send message back to client as JSON
        json_data = json.dumps(data)
        self.wfile.write(json_data.encode('utf-8'))

def run():
    server_address = ('127.0.0.1', 5000)
    httpd = HTTPServer(server_address, RequestHandler)
    print('HTTP server running on port 8080...')
    httpd.serve_forever()

if __name__ == '__main__':
    run()
