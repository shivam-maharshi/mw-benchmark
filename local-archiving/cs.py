import logging
logging.basicConfig(filename='/var/www/html/error.log',level=logging.ERROR)
import os
import os.path
from hashlib import md5
from gevent import monkey
monkey.patch_all()
from gevent.pool import Pool
pool = Pool(35000)
from gevent.pywsgi import WSGIServer

def hello(env, start_response):
    try:
        fp = '/var/www/html/archive/' + md5(env['PATH_INFO'][14:]).hexdigest()
        if not os.path.exists(fp):
            with open(fp, "a") as f:
                f.write(env['wsgi.input'].read())
                f.close()
        start_response('200 OK', [('Content-Type', 'text/html')])
        return 'OK\n'
    except Exception as e:
        logging.error(e)
        start_response('500 ERROR', [('Content-Type', 'text/html')])
        return 'ERROR\n'

server = WSGIServer(('127.0.0.1',8080), hello, spawn=pool)

server.serve_forever()
