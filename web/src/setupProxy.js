// Proxy configuration for development environment
/* eslint-disable @typescript-eslint/no-var-requires */
const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
    ["/api/*", "/oauth2/*", "/connect/*", "/.well-known/*"].forEach(path => {
        app.use(path,
            createProxyMiddleware({
                target: "http://127.0.0.1:8080",
                changeOrigin: true,
                secure: false,
                logLevel: "debug"
            })
        );
    });
}

