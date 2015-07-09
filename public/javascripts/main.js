(function() {
  window.addEventListener('WebComponentsReady', function(e) {
    $(function() {
      setTimeout(function() {
        $('.loading-content-container').addClass('invisible');
          setTimeout(function() {
            $('.loading-content-container').addClass('hidden');
            $('.main-content-container').removeClass('hidden');

            setTimeout(function() {
              $('.main-content-container').removeClass('invisible');
            }, 100);
          }, 200);
      }, 300);
    });
  });
}).call();