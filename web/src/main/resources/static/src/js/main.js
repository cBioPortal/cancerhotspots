$(document).ready(function() {
    var proxy = new HotspotDataProxy();

    proxy.getAllHotspots(function(data) {
        var tableView = new HotspotTableView();
        tableView.render(data);
    });
});
