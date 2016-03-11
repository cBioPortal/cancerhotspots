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
                    // defer rendering of the table a few miliseconds
                    // for a smoother rendering of the loader
                    setTimeout(function(){
                        callback({data: hotspotData});
                    }, 500);
                });
            }
        });

        // render the table
        tableView.render();
    }

    //initWithData();
    initWithAjax();
});
