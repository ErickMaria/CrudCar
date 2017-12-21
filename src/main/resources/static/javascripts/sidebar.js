window.onload = function() {
	if($('.sidebar').children('.sidebar-btn').length > 0){
		$('.sidebar-btn').addClass('sidebar-btn-in');	
	}else{
		$('.sidebar-btn').addClass('sidebar-btn-out');
	}
}

$(document).ready(function(){
	$('.sidebar-btn').click(function(){
		
		if($('.sidebar').hasClass('sidebar-actived')){
			$('.sidebar').toggleClass('sidebar-actived-hidden-visible');
			$('.main').toggleClass('div-main');
			return;
		}
		$('.sidebar').toggleClass('sidebar-hidden-visible');
		$('.main').toggleClass('div-main');
	});
});