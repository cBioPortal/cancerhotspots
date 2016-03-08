function HotspotDataProxy(options)
{
    function getAllHotspots(callback)
    {
        // retrieve data from the server
        $.ajax(ajaxOpts("hotspots", {}, callback));
    }

    function ajaxOpts(url, data, callback)
    {
        return {
            type: "POST",
            url: url,
            data: data,
            success: callback,
            error: function() {
                console.log("Error retrieving cancer hotspots data");
                callback([]);
            },
            dataType: "json"
        };
    }

    this.getAllHotspots = getAllHotspots;
}
