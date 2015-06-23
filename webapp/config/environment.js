/* jshint node: true */

module.exports = function(environment) {
  var ENV = {
    modulePrefix: 'webapp',
    environment: environment,
    baseURL: '/webapp/',
    locationType: 'auto',
    EmberENV: {
      FEATURES: {
        // Here you can enable experimental features on an ember canary build
        // e.g. 'with-controller': true
      }
    },

    APP: {
      // Here you can pass flags/options to your application instance
      // when it is created
    }
  };

  if (environment === 'development') {
    // ENV.APP.LOG_RESOLVER = true;
    // ENV.APP.LOG_ACTIVE_GENERATION = true;
    // ENV.APP.LOG_TRANSITIONS = true;
    // ENV.APP.LOG_TRANSITIONS_INTERNAL = true;
    // ENV.APP.LOG_VIEW_LOOKUPS = true;
    ENV.contentSecurityPolicy = {
      'default-src': "'none'",
      'script-src': "'self' localhost:35729 127.0.0.1:35729 0.0.0.0:35729",
      'font-src': "'self'",
      'connect-src': "'self' ws://localhost:35729 ws://127.0.0.1:35729 ws://0.0.0.0:35729",
      'img-src': "'self'",
      'style-src': "'self'",
      'media-src': "'self'"
    };
  }

  if (environment === 'test') {
    // Testem prefers this...
    ENV.baseURL = '/';
    ENV.locationType = 'none';

    // keep test console output quieter
    ENV.APP.LOG_ACTIVE_GENERATION = false;
    ENV.APP.LOG_VIEW_LOOKUPS = false;

    ENV.APP.rootElement = '#ember-testing';
  }

  if (environment === 'production') {

  }

  return ENV;
};
