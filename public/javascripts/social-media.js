(function() {
  window.addEventListener('WebComponentsReady', function(e) {
    $(function() {
      $('.social-media-mobile a').on('click', function(e) {
        var viewEl = $('.social-media-icons-mobile')

        if(!viewEl.hasClass('open')) {
          viewEl.addClass('open');

          window.setTimeout(function() {
            $('.social-media-icons-mobile .close-icon a').one('click', function(e) {
              viewEl.removeClass('open');
            });
          }, 350);
        }
      });
    });
  });
}).call();