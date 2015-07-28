var phantom = require('phantom')

var url = process.argv[2]
var path = process.argv[3] || ''

phantom.create(function(ph) {
  ph.createPage(function(page) {
    page.open(url + '/' + path, function(status) {
      if(status === 'success') {
        page.evaluate(function() {
          var content = document.body.getElementsByClassName('main-content-container')
          var text = content[0].textContent.replace(/[\n\r]/g, '').replace(/\s{2,}/g, ' ').trim()
          return {title: document.title, content: text}
        }, function(result) {
          console.log(">>>>>>>>>>>>> OUTPUT START")
          console.log(JSON.stringify(result))
          console.log(">>>>>>>>>>>>> OUTPUT END")
          ph.exit()
        })
      }
      else {
        console.log('Failed to open page (' + status + ')')
        ph.exit()
      }
    })
  })
})
