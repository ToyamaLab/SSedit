$(function() {
	function pageselectCallback(page_index, jq){
		var new_content = $('#hiddenresult div.result:eq('+page_index+')').clone();
		$('#res').empty().append(new_content);
		return false;
	}
	function initPagination() {
		var num_entries = $('#hiddenresult div.result').length;
		// Create pagination element
		$("#Pagination").pagination(num_entries, {
			num_edge_entries: 2,
			num_display_entries: 8,
			callback: pageselectCallback,
			items_per_page:1
		});
	}
	$(function(){
		initPagination();
	});
});