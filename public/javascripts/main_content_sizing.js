(function() {
  $(function() {
    var resizeMainContent = function() {
      var height = $(window).height();

      if($(window).width() > 1023)
        height -= 200;
      else
        height -= 100;

      $('#main-content').css('max-height', height);
    };

    $(window).resize(resizeMainContent);

    resizeMainContent();
  });
}).call();