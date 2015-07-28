(function() {
  window.addEventListener('WebComponentsReady', function(e) {
    $(function() {
      setTimeout(function() {
        $('.loading-content-container').addClass('invisible')
          setTimeout(function() {
            $('.loading-content-container').addClass('hidden')
            $('.main-content-container').removeClass('hidden')

            setTimeout(function() {
              $('.main-content-container').removeClass('invisible')
            }, 100)
          }, 200)
      }, 300)
    })

    var searchEls   = $('.nav-right input[type="search"], .toolbar-search-input-mobile input')
    var searchBtnEl = $('.nav-right .toolbar-search-btn')

    searchBtnEl.on('click', function() {
      var searchString = $('.nav-right input[type="search"]').val()
      executeSearch(searchString)
    })

    searchEls.on('keyup', function(event) {
      if(event.keyCode === 13) {
        var searchString = $('.nav-right input[type="search"]').val()

        if(searchString == '') {
          searchString = $('.toolbar-search-input-mobile input').val()
        }
        executeSearch(searchString)
      }
    })

    var executeSearch = function(searchString) {
      var form = document.createElement('form')
      form.setAttribute('method', 'post')
      form.setAttribute('action', '/search')

      var s = document.createElement('input')
      s.setAttribute('name', 'searchString')
      s.setAttribute('type', 'text')
      s.setAttribute('value', searchString)

      form.appendChild(s)
      form.submit()
    }
  })
}).call()
