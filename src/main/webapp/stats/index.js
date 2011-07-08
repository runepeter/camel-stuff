$(document).ready(function() {
    new Monitor();
});

function Monitor() {
    var jmx = new Jolokia("/jolokia");

    var memoryChart = new Highcharts.Chart({
        chart: {
            renderTo: 'memoryChart',
            defaultSeriesType: 'spline',
            events: {
                load: function() {
                    var series = this.series[0];
                    setInterval(function() {
                        var x = (new Date()).getTime();
                        var memoryUsed = jmx.getAttribute("java.lang:type=Memory", "HeapMemoryUsage", "used");
                        series.addPoint({
                            x: new Date().getTime(),
                            y: parseInt(memoryUsed)
                        }, true, series.data.length >= 50);
                    }, 5000);
                }
            }
        },
        title: { text: 'memory' },
        xAxis: {
            type: 'datetime'
        },
        yAxis: {
				title: { text: 'number' }
			},
        series: [{
                data: []
            }
        ]
    });

    var threadChart = new Highcharts.Chart({
        chart: {
            renderTo: 'threadChart',
            defaultSeriesType: 'spline',
            events: {
                load: function() {
                    var series = this.series[0];
                    setInterval(function() {
                        var x = (new Date()).getTime();
                        var memoryUsed = jmx.getAttribute("java.lang:type=Threading", "ThreadCount");
                        series.addPoint({
                            x: new Date().getTime(),
                            y: parseInt(memoryUsed)
                        }, true, series.data.length >= 50);
                    }, 5000);
                }
            }
        },
        title: { text: 'threads' },
        xAxis: {
            type: 'datetime'
        },
        yAxis: {
				title: { text: 'number' }
			},
        series: [{
                data: []
            }
        ]
    });


    var systemChart = new Highcharts.Chart({
        chart: {
            renderTo: 'systemChart',
            defaultSeriesType: 'spline',
            events: {
                load: function() {
                    var series = this.series[0];
                    setInterval(function() {
                        var x = (new Date()).getTime();
                        var memoryUsed = jmx.getAttribute("java.lang:type=OperatingSystem", "SystemLoadAverage");
                        series.addPoint({
                            x: new Date().getTime(),
                            y: parseInt(memoryUsed)
                        }, true, series.data.length >= 50);
                    }, 5000);
                }
            }
        },
        title: { text: 'system' },
        xAxis: {
            type: 'datetime'
        },
        yAxis: {
				title: { text: 'load' }
			},
        series: [{
                data: []
            }
        ]
    });

}