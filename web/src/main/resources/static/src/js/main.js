$(document).ready(function() {
    var proxy = new HotspotDataProxy();

    // get all hotspot data
    proxy.getAllHotspots(function(data) {
        var mainTemplateFn = _.template($("#main_view").html());
        $("#main_content").html(mainTemplateFn());

        // init the table view with the hotspot data
        var tableView = new HotspotTableView({
            data: data
        });

        // render the table
        tableView.render();
    });
});
