var page = 0;

loadMails();

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
    $.get('/mails/page/'+page, function(array) {
    	$.each(array, function(index, mail) {
       		if(mail.seen == 'true') {
            tbody.append('<tr id="row'+mail.id+'"><td>'+mail.id+'</td><td>'+mail.subject+'</td><td>'+mail.from+'</td><td>'+mail.date+'</td></tr>');
       		}else{
            tbody.append('<tr id="row'+mail.id+'" class="unread"><td>'+mail.id+'</td><td>'+mail.subject+'</td><td>'+mail.from+'</td><td>'+mail.date+'</td></tr>');
       		}
        	$("#row" + mail.id).click(function() {
          		$.get('/mails/' + mail.id, function(mail) {
          			$('.table').hide();
          			$('#Prev').hide();
          			$('#Next').hide();
            		$('#detail').show();
            		var content = $('#content');
            		content.html("");
            		content.append('<h3>From : '+mail.from+'</h3><h3>To : '+mail.to+'</h3><h3>Subject : '+mail.subject+'</h3><h3>Date : '+mail.date+'</h3><br><p>'+mail.body+'</p>');
	          	});
    		});
       	});
     });
    });
}

function showMail(index){
	'<h5>Subject: ' + mail.subject + '</h5><h5>From: ' + mail.from +'</h5><h5>Date: ' +mail.date+'</h5><h5>To: ' +mail.to+ '</h5><h6>' + mail.body + '</h6>';
    return '';
}
