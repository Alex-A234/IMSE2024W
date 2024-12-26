#!/bin/sh
sed -i -e 's;_BACKEND_SERVICE_;'$BACKEND_SERVICE';' /etc/nginx/conf.d/default.conf
exec "$@"