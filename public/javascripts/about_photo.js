(function() {
  $(function() {
    var opened = false;
    var page = $('#photo-page-value').val();
    var url = "/background/details/" + page

    var photoBoxResize = function() {
      if(opened) {
        $('img.background-details-image').css('max-width', $(window).width() - 100);
        $('img.background-details-image').css('height', $(window).height() - 180);

        photoBox.setWidth($('img.background-details-image').width() + 30);
        photoBox.setHeight($('img.background-details-image').height() + 110);

        photoBox.position();
      }
    };

    var photoBox = new jBox('Modal', {
      animation:      'zoomIn',
      reposition:     true,
      width:          500,
      attach:         $('#about-photo-show'),
      title:          'Loading photo content&nbsp;<img src="/assets/images/loading.gif"/>',
      content:        $('#photo-modal-content'),
      ajax:           {
        url:          url,
        reload:       false,
        setContent:   true,
        spinner:      true,
        complete:     function() {
          this.setTitle($('#image-details-title').val());
          photoBoxResize()
        },
        error:    function() {
          $('#photo-modal-content').html("So sorry, but there appears to be something wrong with the loading of this content, please try again in a few minutes.");
        }
      },
      onOpen:         function() {
        opened = true;
        setTimeout(photoBoxResize, 500);
      },
      onClose:        function() {
        opened = false;
      }
    });

    $(window).resize(function() {
      photoBoxResize();
    });
  });
}).call();
