$(function(){
	$("#tableselector").change(function(){
		tname=$(this).val();
		$(".tableinfodiv").hide();
		tdiv = "tdiv_" + tname;
		$("#" + tdiv).show();
	});
});
