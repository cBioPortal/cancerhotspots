$(document).ready(function() {
    function initWithData()
    {
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
    }

    function initWithAjax()
    {
        var mainTemplateFn = _.template($("#main_view").html());
        $("#main_content").html(mainTemplateFn());

        // init the table view with the hotspot data retrieval function
        var tableView = new HotspotTableView({
            "ajax": function (data, callback, settings) {
                var proxy = new HotspotDataProxy();

                proxy.getAllHotspots(function(hotspotData) {
                    callback({data: hotspotData});
                });
            }
        });

        // render the table
        tableView.render();
    }

    //initWithData();
    initWithAjax();
});
