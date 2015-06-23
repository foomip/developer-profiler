(function() {
  $(function() {
    $.ajax({
      url:        '/assets/markdowns/index.md',
      type:       'GET',
      dataType:   'text',
      success:    function(data) {
        $('#main-content').html(markdown.toHTML(data));
      },
      error:      function(jqXHR, status, error) {
        console.log(error);
      }
    });
  });
}).call();
