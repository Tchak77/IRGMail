var page = 0;
var lastGet = "/mails/page/0";

var start = new Date().getTime();
while(new Date().getTime() - start < 3000){}
loadMails();
setInterval("update()", 10000);

function update(){
  $(document).ready(function() {
    var tbody = $("#mails");
    tbody.html("");
    $.get(lastGet, function(array) {
      $.each(array, function(index, mail) {
          showMail(mail);
        });
     });
  });  
}

function previous(){
  $(document).ready(function() {
    var tbody = $("#mails");
    $('.table').show();
    $('#detail').hide();
    if(lastGet.indexOf("search") == -1){
        $('#Prev').show();
        $('#Next').show();
    }
    tbody.html("");
    $.get(lastGet, function(array) {
      $.each(array, function(index, mail) {
          showMail(mail);
        });
     });
  });
}

function pageDown(){
	if(page > 0){
		page--;
		loadMails();
	}
};

function pageUp(){
	$(document).ready(function() {
		var row1 = $("#row1");
		if(!row1.length){
			page++;
			loadMails();
		}
	});
};

function loadMails(){
	$(document).ready(function() {
    var tbody = $("#mails");
  	$('.table').show();
  	$('#Prev').show();
  	$('#Next').show();
  	$('#detail').hide();
    tbody.html("");
    lastGet = "/mails/page/"+page;
    $.get('/mails/page/'+page, function(array) {
    	$.each(array, function(index, mail) {
          showMail(mail);
       	});
     });
  });
}

function showMail( mail ){
  $(document).ready(function() {
    var tbody = $("#mails");
    if(mail.seen == 'true') {
        tbody.append('<tr id="row'+mail.id+'"><td>'+mail.id+'</td><td>'+mail.subject+'</td><td>'+mail.from+'</td><td>'+mail.date+'</td></tr>');
    }else{
        tbody.append('<tr id="row'+mail.id+'" class="unread"><td>'+mail.id+'</td><td>'+mail.subject+'</td><td>'+mail.from+'</td><td>'+mail.date+'</td></tr>');
    }
    $("#row" + mail.id).click(function() {
      $.get('/mails/' + mail.id, function(content){
          showContent(content);
      });
    });
  });
}

function showContent( mail ){
  $(document).ready(function() {
    $('.table').hide();
    $('#Prev').hide();
    $('#Next').hide();
    $('#detail').show();
    var content = $('#content');
    content.html("");
    content.append('<h3>From : '+mail.from+'</h3><h3>To : '+mail.to+'</h3><h3>Subject : '+mail.subject+'</h3><h3>Date : '+mail.date+'</h3><br><p>'+mail.body+'</p>'); 
  });
}

function search(){
  $(document).ready(function() {
    var tbody = $("#mails");
    $('.table').show();
    $('#Prev').hide();
    $('#Next').hide();
    $('#detail').hide();
    var keywords = $("#searchInput").val();
    if(keywords.length){
      tbody.html("");
      lastGet = "/mails/search/"+keywords;
      $.get('/mails/search/'+keywords, function(array) {
        $.each(array, function(index, mail) {
            showMail(mail);
        });
      });
    }
  });
}