/**
 *  id : id of the div where the view will be deploy
 *  additionnalIndicators : Array list of :
 *  {
 *      name : name,
 *      value : value,
 *      color : color
 *  }
 *  additionnalIndicators allow you to add a visual indicator on waterfall, by default, onload value is showed
 */

function HarViewer(id, additionnalIndicators) {

    // Estado para ordenación
    this.lastSortColumn = null;
    this.lastSortDirection = 1; // 1: asc, -1: desc

    // Sincronización de hover entre fila y waterfall
    function syncHoverEvents() {
        // Hover en filas de la tabla
        $(document).on('mouseenter', '.HarViewerTableRow', function () {
            var idx = $(this).attr('id').split('_')[1];
            $('#HarViewerWaterFallRow_' + idx).addClass('HarViewerRowHovered');
        });
        $(document).on('mouseleave', '.HarViewerTableRow', function () {
            var idx = $(this).attr('id').split('_')[1];
            $('#HarViewerWaterFallRow_' + idx).removeClass('HarViewerRowHovered');
        });
        // Hover en waterfall sincroniza con la fila
        $(document).on('mouseenter', '.HarViewerWaterFallRow', function () {
            var idx = $(this).attr('id').split('_')[1];
            $('#HarViewerRow_' + idx).addClass('HarViewerRowHovered');
        });
        $(document).on('mouseleave', '.HarViewerWaterFallRow', function () {
            var idx = $(this).attr('id').split('_')[1];
            $('#HarViewerRow_' + idx).removeClass('HarViewerRowHovered');
        });
    }
    syncHoverEvents();

    // Ocultar panel de detalles al crear el viewer (por defecto oculto hasta selección)
    setTimeout(function() {
        var details = document.getElementById('HarViewerDetails');
        if (details) {
            details.style.display = 'none';
        }
    }, 0);
    var viewer = this;
    this.id = id;

    // Establecer el alto al 100% del contenedor padre
    this.adjustContainerHeights = function () {
        var el = document.getElementById(viewer.id);
        var infoBar = document.getElementById('HarViewerGlobalInformationsBar');
        var form = document.getElementById('HarViewerForm');
        var content = document.getElementById('HarViewerContent');
        if (el) {
            el.style.height = '100%';
            el.style.boxSizing = 'border-box';
        }
        if (content && el) {
            // Calcular el alto disponible descontando HarViewerForm y la info bar
            var infoBarHeight = infoBar ? infoBar.offsetHeight : 0;
            var formHeight = form ? form.offsetHeight : 0;
            var elHeight = el.offsetHeight;
            var contentHeight = elHeight - infoBarHeight - formHeight;
            if (contentHeight > 0) {
                content.style.height = contentHeight + 'px';
            } else {
                content.style.height = '100%';
            }
            content.style.overflowY = 'auto';
            content.style.boxSizing = 'border-box';
        }
    };

    function setHeightsOnResize() {
        viewer.adjustContainerHeights();
    }

    setTimeout(setHeightsOnResize, 0);
    window.addEventListener('resize', setHeightsOnResize);

    this.entriesSummaries = new Array();
    this.showedEntries = new Array();

    this.indicators = new Array();
    if (additionnalIndicators) {

        for (var i = 0; i < additionnalIndicators.length; i++) {
            this.indicators.push(addtionnalIndicator[i]);
        }

    }
    this.form = null;
    this.search = null;
    this.content = null;
    this.left = null;
    this.right = null;
    this.table = null;
    this.listFilters = ['Hour', 'URL', 'Method', 'Status', 'Type', 'Size', 'Time'];
    this.responseHeader = ['Server', 'Date', 'Content-Type', 'Last-Modified', 'Transfer-Encoding', 'Connection', 'Vary', 'Content-Encoding'];
    this.requestHeader = ['Host', 'Accept', 'Referer', 'Accept-Encoding', 'Accept-Language', 'User-Agent', 'Cache-Control'];
    this.entryTimings = ['blocked', 'dns', 'connect', 'ssl', 'send', 'wait', 'receive'];
    this.filters = null;
    this.time = null;
    this.firstDate = null;
    this.waterfall = null;
    this.timesTh = null;
    this.details = null;
    this.request = null;
    this.requestList = null;
    this.response = null;
    this.responseList = null;
    this.timings = null;
    this.timingsList = null;
    this.globalInformationBar = null;
    this.buttons = null;
    this.reload = null;
    this.displayWaterFall = null;
    this.displayDetails = null;
    this.firstDetails = true;
    this.currentEntryFocused = null;

    this.colorPairRow = '#FFFFFF';
    this.colorUnpairRow = '#f6f7f8';
    this.colorHighlightRow = '#FFA07A';
    this.colorIndicatorOnload = '#DC143C';

    this.loadHar = function (har) {
        this.entriesSummaries = new Array();
        this.har = har;
        this.entries = parseHar(har);
        this.initEntriesSummaries();
        var onload = this.har.log.pages[0].pageTimings.onLoad;

        this.indicators.push({ name: 'onload', value: onload, color: this.colorIndicatorOnload });
        this.currentEntryFocused = null;
        this.initFrontEnd();

    }

    this.initEntriesSummaries = function () {

        this.time = 0;

        for (var i = 0; i < this.entries.length; i++) {

            // beautify the url to make a name of the resources

            var url = this.entries[i].request.url;

            var c = 0;
            var find = false;
            for (var j = url.length - 1; (j >= 0 && !find); j--) {
                if (url[j] == '/') {
                    c = j;
                    find = true;
                }

            }

            var name = url.substring(c);

            // beautify the type

            var brutType = this.entries[i].response.content.mimeType;
            var d = 0;
            find = false;
            for (var j = 0; (j < brutType.length && !find); j++) {
                if (brutType[j] == ';') {
                    d = j;
                    find = true;
                }

            }
            var type = brutType.substring(0, d);
            if (!find) {
                type = brutType;
            }

            // Asignar timings en el orden correcto
            var timingsRaw = this.entries[i].timings;
            var timingsOrdered = {
                blocked: timingsRaw.blocked,
                dns: timingsRaw.dns,
                connect: timingsRaw.connect,
                ssl: timingsRaw.ssl,
                send: timingsRaw.send,
                wait: timingsRaw.wait,
                receive: timingsRaw.receive
            };
            entry = {
                Name: name,
                URL: this.entries[i].request.url,
                Method: this.entries[i].request.method,
                Status: this.entries[i].response.status,
                Type: type,
                Size: this.entries[i].response.content.size,
                Time: this.entries[i].time,
                Timings: timingsOrdered,
                date: new Date(this.entries[i].startedDateTime).valueOf()
            };
            delete entry.Timings.comment;
            // No borrar ssl, se necesita para el gráfico
            this.showedEntries.push(entry);
            this.entriesSummaries.push(entry);
        }

    }

    this.initFrontEnd = function () {

        this.initForm();
        this.initContent();
        // Global info now shown next to Search field

    }

    this.initButtonReload = function () {
        this.reload = document.createElement('img');
        this.reload.id = 'HarViewerReload';
        this.reload.src = 'assets/HAR-Viewer-dev/reload.png';
        this.reload.style.filter = 'invert(32%) sepia(0%) saturate(0%) hue-rotate(180deg) brightness(95%) contrast(90%)';
        // #6A6a6a aproximado con filtro
        $('#' + this.buttons.id).append(this.reload);
        this.reload.style.height = '25px';
        this.reload.style.width = '25px';
        $('#' + this.reload.id).click(function (event) {
            viewer.showedEntries = new Array();
            for (var i = 0; i < viewer.entriesSummaries.length; i++) {
                viewer.showedEntries.push(viewer.entriesSummaries[i]);
            }
            viewer.initTime();
            viewer.initTable();
            viewer.initWaterFallView();
            viewer.hideDetails();
            viewer.showWaterFall();
        })
    }

    this.initButtonDisplayWaterFall = function () {
        this.displayWaterFall = document.createElement('img');
        this.displayWaterFall.id = 'HarViewerDisplayWaterFall';
        this.displayWaterFall.src = 'assets/HAR-Viewer-dev/displayWaterFall.png';
        this.displayWaterFall.style.filter = 'invert(32%) sepia(0%) saturate(0%) hue-rotate(180deg) brightness(95%) contrast(90%)';
        // #6A6a6a aproximado con filtro
        $('#' + this.buttons.id).append(this.displayWaterFall);
        this.displayWaterFall.style.height = '25px';
        this.displayWaterFall.style.width = '25px';
        $('#' + this.displayWaterFall.id).click(function (event) {
            viewer.hideDetails();
            viewer.showWaterFall();
        })
    }

    this.initButtonDisplayDetails = function () {
        this.displayDetails = document.createElement('img');
        this.displayDetails.id = 'HarViewerDisplayDetails';
        this.displayDetails.src = 'assets/HAR-Viewer-dev/displayDetails.png';
        this.displayDetails.style.filter = 'invert(32%) sepia(0%) saturate(0%) hue-rotate(180deg) brightness(95%) contrast(90%)';
        // #6A6a6a aproximado con filtro
        $('#' + this.buttons.id).append(this.displayDetails);
        this.displayDetails.style.height = '25px';
        this.displayDetails.style.width = '25px';
        $('#' + this.displayDetails.id).click(function (event) {
            viewer.hideWaterFall();
            viewer.showDetails();
        })
    }

    this.initButtons = function () {

        this.buttons = document.createElement('div');
        this.buttons.id = 'HarViewerActions';
        $('#' + this.form.id).append(this.buttons);

        this.initButtonReload();
        this.initButtonDisplayWaterFall();
        this.initButtonDisplayDetails();

        var position = $('#' + this.form.id).position();
        var width = $('#' + this.reload.id).outerWidth(true) + $('#' + this.displayWaterFall.id).outerWidth(true) + $('#' + this.displayDetails.id).outerWidth(true);
        $('#' + this.buttons.id).width(width + 10);
        var x = position.left + $('#' + this.form.id).width() - $('#' + this.buttons.id).outerWidth(true);
        var y = position.top + $('#' + this.form.id).css('padding-top') + $('#' + this.form.id).css('margin-top');

        $('#' + this.buttons.id).css('position', 'absolute');
        $('#' + this.buttons.id).css('top', y);
        $('#' + this.buttons.id).css('left', x);

    }

    this.initSearchBar = function () {

        this.search = document.createElement('input');
        this.search.type = 'text';
        this.search.id = 'HarViewerSearch';
        this.search.placeholder = 'Search...';
        $('#' + this.form.id).append(this.search);
        // Usar input en vez de keypress para refrescar búsqueda al borrar caracteres
        $('#' + this.search.id).on('input', this.onSearch);
        //$('#'+this.search.id).click( this.onClickOnSearch );
        //$('#'+this.search.id).focusout( this.onFocusOutSearch );

        $('#' + this.search.id).css('border-radius', ($('#' + this.search.id).outerHeight() / 2));
        $('#' + this.search.id).css('border-color', '#000000');
        $('#' + this.search.id).css('border-width', 0);
        $('#' + this.search.id).css('border-style', 'solid');
        // Add requests/weight info next to Search field
        var infoSpan = document.createElement('span');
        infoSpan.id = 'HarViewerInfoBar';
        infoSpan.style.marginLeft = '16px';
        var weight = 0;
        for (var i = 0; i < viewer.entriesSummaries.length; i++) {
            weight += viewer.entriesSummaries[i].Size;
        }
        var sizeStr = '';
        if (weight < 1024) {
            sizeStr = weight + ' B';
        } else if (weight < 1024 * 1024) {
            sizeStr = (weight / 1024).toFixed(1) + ' KB';
        } else {
            sizeStr = (weight / (1024 * 1024)).toFixed(1) + ' MB';
        }
        infoSpan.innerHTML = viewer.entriesSummaries.length + ' requests (' + sizeStr + ')';
        $('#' + this.form.id).append(infoSpan);

    }

    this.initForm = function () {

        this.form = document.createElement('form');
        this.form.id = 'HarViewerForm';

        if ($('#' + this.form.id).length) {
            $('#' + this.form.id).remove();
        }

        $('#' + this.id).append(this.form);

        this.initSearchBar();
        this.initButtons();

        //$('#'+this.form.id).outerWidth($('#'+this.id).outerWidth());
        $('#' + this.form.id).height($('#' + this.search.id).outerHeight());

    }

    this.initLeft = function () {
        this.left = document.createElement('div');
        this.left.id = 'HarViewerLeftPanel';
        $('#' + this.content.id).append(this.left);
        $('#' + this.left.id).css('float', 'left');
        this.initTable();
    }

    this.initRight = function () {
        this.right = document.createElement('div');
        this.right.id = 'HarViewerRightPanel';
        $('#' + this.content.id).append(this.right);
        $('#' + this.right.id).css('float', 'right');
        //$('#'+this.right.id).outerWidth(($('#'+this.content.id).width() - $('#'+this.left.id).outerWidth(true)));
        this.initTime();
        this.initWaterFallView();
        this.currentEntryFocused = null;
        this.initDetails();
        this.hideDetails();
        this.showWaterFall();

    }

    this.initContent = function () {
        this.content = document.createElement('div');
        this.content.id = 'HarViewerContent';
        if ($('#' + this.content.id).length) {
            $('#' + this.content.id).remove();
        }
        $('#' + this.id).append(this.content);

        this.initLeft();

        $('#' + this.content.id).height($('#' + this.left.id).outerHeight(true));
        //$('#'+this.content.id).outerWidth($('#'+this.form.id).outerWidth(true));

        this.initRight();
    }

    this.initTable = function () {

        this.table = document.createElement('div');
        this.table.id = 'HarViewerTable';

        if ($('#' + this.table.id).length) {
            $('#' + this.table.id).remove();
        }

        $('#' + this.left.id).append(this.table);

        // Si no hay entries, mostrar mensaje y no mostrar filtros
        if (!this.entries || this.entries.length === 0) {
            this.table.innerHTML = '<div style="padding:16px;color:#888;text-align:left;">No requests found.</div>';
            return;
        }

        this.initFilterBar();

    }

    this.initFilterBar = function () {

        // Si no hay entries, no crear filtros
        if (!this.entries || this.entries.length === 0) {
            return;
        }

        this.filters = document.createElement('div');
        this.filters.id = 'HarViewerFilters';
        $('#' + this.table.id).append(this.filters);

        for (var i = 0; i < this.listFilters.length; i++) {
            var filter = document.createElement('div');
            filter.id = 'HarViewerFilters_' + i;
            filter.innerHTML = this.listFilters[i];
            $('#' + this.filters.id).append(filter);
            $('#' + filter.id).addClass(filter.id);
            $('#' + filter.id).addClass(this.filters.id + '' + this.listFilters[i]);
            // Permitir orden para todas las columnas, incluyendo Hour
            $('#' + filter.id).click(this.onFilter);
        }

        this.initEntriesRows();

    }

    this.initTime = function () {

        var a = this.showedEntries[0].date;
        var b = 0;
        var c = 0;
        for (var i = 0; i < this.showedEntries.length; i++) {
            if (this.showedEntries[i].date < a) {
                a = this.showedEntries[i].date;
            }
            if (this.showedEntries[i].date >= b) {
                if (this.showedEntries[i].date == b) {
                    c = Math.max(this.showedEntries[i].Time, c);
                } else {
                    c = this.showedEntries[i].Time;
                }
                b = this.showedEntries[i].date;
            }
        }
        this.time = (b - a) + c;
        this.firstDate = a;
    }

    this.initEntriesRows = function () {

        var viewer = this;
        var total = this.showedEntries.length;
        var batchSize = 10;
        var table = $('#' + this.table.id);
        function renderBatch(start) {
            var end = Math.min(start + batchSize, total);
            for (var i = start; i < end; i++) {
                var entry = viewer.showedEntries[i];
                var row = document.createElement('div');
                row.id = 'HarViewerRow_' + i;
                row.dataset.realIndex = entry._realIndex !== undefined ? entry._realIndex : viewer.entriesSummaries.indexOf(entry);
                table.append(row);
                $(row).addClass('HarViewerTableRow');
                if (i % 2 == 0) {
                    $(row).css('background-color', viewer.colorPairRow);
                } else {
                    $(row).css('background-color', viewer.colorUnpairRow);
                }
                // Primera columna: Hour
                var Hour = '';
                if (entry.URL && entry.date) {
                    var d = new Date(entry.date);
                    var hh = String(d.getHours()).padStart(2, '0');
                    var mm = String(d.getMinutes()).padStart(2, '0');
                    var ss = String(d.getSeconds()).padStart(2, '0');
                    Hour = hh + ':' + mm + ':' + ss;
                }
                var HourField = document.createElement('div');
                HourField.id = 'HarViewerRow_Hour_' + i;
                HourField.innerHTML = '<p title="' + Hour + '" class="HarViewerTooltip">' + Hour + '</p>';
                row.appendChild(HourField);
                HourField.dataset.realIndex = row.dataset.realIndex;
                $(HourField).addClass('HarViewerFiltersHour');
                $(HourField).click(viewer.onEntry);
                // Resto de columnas
                for (var j = 1; j < viewer.listFilters.length; j++) {
                    var field = document.createElement('div');
                    field.id = 'HarViewerRow_' + viewer.listFilters[j] + '_' + i;
                    var value = entry[viewer.listFilters[j]];
                    if (viewer.listFilters[j] === 'Size') {
                        var sizeStr = '';
                        if (value < 1024) {
                            sizeStr = value + ' B';
                        } else if (value < 1024 * 1024) {
                            sizeStr = (value / 1024).toFixed(1) + ' KB';
                        } else if (value < 1024 * 1024 * 1024) {
                            sizeStr = (value / (1024 * 1024)).toFixed(1) + ' MB';
                        } else {
                            sizeStr = (value / (1024 * 1024 * 1024)).toFixed(1) + ' GB';
                        }
                        field.innerHTML = '<p title="' + value + '" class="HarViewerTooltip">' + sizeStr + '</p>';
                    } else if (viewer.listFilters[j] === 'Time') {
                        var timeStr = '';
                        if (value < 1000) {
                            timeStr = value + ' ms';
                        } else {
                            timeStr = (value / 1000).toFixed(1) + ' s';
                        }
                        field.innerHTML = '<p title="' + value + '" class="HarViewerTooltip">' + timeStr + '</p>';
                    } else {
                        field.innerHTML = '<p title="' + value + '" class="HarViewerTooltip">' + value + '</p>';
                    }
                    row.appendChild(field);
                    field.dataset.realIndex = row.dataset.realIndex;
                    $(field).addClass('HarViewerFilters' + viewer.listFilters[j]);
                    $(field).click(viewer.onEntry);
                }
            }
            if (end < total) {
                setTimeout(function () { renderBatch(end); }, 0);
            }
        }
        renderBatch(0);
    }

    this.initWaterFallView = function () {

        this.waterfall = document.createElement('div');
        this.waterfall.id = 'HarViewerWaterFall';

        if ($('#' + this.waterfall.id).length) {
            $('#' + this.waterfall.id).remove();
        }



        $('#' + this.right.id).append(this.waterfall);

        this.initWaterFallHeader();
        this.showWaterFall();
        this.initIndicators();

    }

    this.initWaterFallHeader = function () {

        this.timesTh = document.createElement('div');
        this.timesTh.id = 'HarViewerTimeTh';

        $('#' + this.waterfall.id).append(this.timesTh);
        $('#HarViewerTimeTh').height($('#' + this.filters.id).height());

        var divTime = Math.floor(this.time / 10);

        for (var i = 0; i < 10; i++) {
            var timeValue = divTime * i;
            var timeLabel = '';
            timeLabel = (timeValue / 1000).toFixed(1) + 's';

            var timeDiv = document.createElement('div');
            timeDiv.id = 'HarViewerTimeTh_' + i;
            timeDiv.innerHTML = timeLabel;
            $('#HarViewerTimeTh').append(timeDiv);
            $('#' + timeDiv.id).addClass('HarViewerTimeHeader');
        }

        this.initWaterFallRows();
    }

    this.initWaterFallRows = function () {

        var beginAt = 0;

        for (var i = 0; i < this.showedEntries.length; i++) {

            var entry = this.showedEntries[i];



            var row = document.createElement('div');
            row.id = 'HarViewerWaterFallRow_' + i;

            $('#' + this.waterfall.id).append(row);
            var width = $('#' + this.waterfall.id).width();
            var trWidth = 0
            $('#HarViewerWaterFallRow_' + i).addClass('HarViewerWaterFallRow');
            beginAt = $('#' + this.waterfall.id).width() * (entry.date - this.firstDate) / this.time;
            $('#HarViewerWaterFallRow_' + i).css('padding-left', beginAt);
            $('#HarViewerWaterFallRow_' + i).outerHeight($('#HarViewerRow_0').outerHeight());
            if (i % 2 == 0) {
                $('#HarViewerWaterFallRow_' + i).css('background-color', this.colorPairRow);
            } else {
                $('#HarViewerWaterFallRow_' + i).css('background-color', this.colorUnpairRow);
            }

            allTimingsBlock = document.createElement('div');
            allTimingsBlock.id = 'HarViewerWaterFallRowAllTiming_' + i;
            allTimingsBlock.title = '';

            for (timing in entry.Timings) {
                value = entry.Timings[timing];

                allTimingsBlock.title = allTimingsBlock.title + timing + ' : ' + value + '\n';
            }

            $('#HarViewerWaterFallRow_' + i).append(allTimingsBlock);
            $('#HarViewerWaterFallRowAllTiming_' + i).addClass('HarViewerAllTimingBlock');
            $('#HarViewerWaterFallRowAllTiming_' + i).addClass('HarViewerTooltip');

            for (timing in entry.Timings) {

                value = entry.Timings[timing];
                var block = document.createElement('div');
                block.id = 'HarViewerWaterFallRow_' + timing + '_' + i;
                block.alt = value;
                $('#HarViewerWaterFallRowAllTiming_' + i).append(block);
                var classTiming = timing.substring(0, 1).toUpperCase() + timing.substring(1);
                $('#HarViewerWaterFallRow_' + timing + '_' + i).addClass('HarViewerTiming' + classTiming);
                $('#HarViewerWaterFallRow_' + timing + '_' + i).addClass('HarViewerTimingBlock');
                var blockWidth = width * value / this.time;
                // Asignar ancho mínimo para que se vea aunque el valor sea pequeño
                $('#HarViewerWaterFallRow_' + timing + '_' + i).outerWidth(Math.max(blockWidth, 2));
                //trWidth = trWidth + $('#HarViewerWaterFallRow_'+timing+'_'+i).outerWidth();


            }

            //beginAt = trWidth + beginAt;

        }

    }

    this.initIndicators = function () {

        for (var i = 0; i < this.indicators.length; i++) {

            var name = this.indicators[i].name;
            var value = this.indicators[i].value;
            var color = this.indicators[i].color;
            var beginAt = $('#' + this.waterfall.id).width() * (value / this.time);

            var position = $('#HarViewerTimeTh').position();

            var x = beginAt + position.left;
            console.log(position.top);
            var y = position.top + $('#HarViewerTimeTh').outerHeight(true);

            var indicator = document.createElement('div');
            indicator.id = 'HarViewerIndicator' + name;
            indicator.title = 'Name : ' + name + ', Value : ' + value;
            $('#' + this.waterfall.id).append(indicator);
            $('#' + indicator.id).width(0);
            $('#' + indicator.id).height(($('#' + this.waterfall.id).height() - $('#HarViewerTimeTh').outerHeight(true)));
            $('#' + indicator.id).css('position', 'absolute');
            $('#' + indicator.id).css('z-index', 6);
            $('#' + indicator.id).css('float', 'left');
            $('#' + indicator.id).css('top', y);
            $('#' + indicator.id).css('left', x);
            $('#' + indicator.id).css('padding', 0);
            $('#' + indicator.id).css('margin', 0);
            $('#' + indicator.id).css('border-right', 'solid');
            $('#' + indicator.id).css('border-width', '2px');
            $('#' + indicator.id).css('border-color', color);
        }
    }

    // Función auxiliar para renderizar la sección de cookies
    function renderCookiesSection(parentId, cookies, sectionId) {
        var cookiesDiv = document.createElement('div');
        cookiesDiv.id = sectionId;
        cookiesDiv.innerHTML = '<p><strong>Cookies</strong></p>';
        var cookiesList = document.createElement('ul');
        cookiesList.id = sectionId + 'List';
        if (cookies && cookies.length > 0) {
            for (var i = 0; i < cookies.length; i++) {
                var cookie = cookies[i];
                var cookieFields = [];
                for (var key in cookie) {
                    if (cookie.hasOwnProperty(key) && cookie[key] !== null && cookie[key] !== '' && cookie[key] !== false) {
                        cookieFields.push('<strong>' + key + ':</strong> ' + cookie[key]);
                    }
                }
                if (cookieFields.length > 0) {
                    var li = document.createElement('li');
                    li.innerHTML = cookieFields.join(', ');
                    cookiesList.appendChild(li);
                }
            }
        } else {
            var li = document.createElement('li');
            li.innerHTML = '<span style="color:#888;">No cookies</span>';
            cookiesList.appendChild(li);
        }
        cookiesDiv.appendChild(cookiesList);
        $('#' + parentId).append(cookiesDiv);
    }

    this.initRequest = function () {
        this.request = document.createElement('div');
        this.request.id = 'HarViewerDetailsRequest';
        var entry = this.entriesSummaries[this.currentEntryFocused];
        var entryComplete = this.entries[this.currentEntryFocused];

        // General section
        var generalItems = [];
        if (entryComplete.request && entryComplete.request.url) {
            generalItems.push('<li><strong>URL:</strong> ' + entryComplete.request.url + '</li>');
        }
        if (entryComplete.request && entryComplete.request.httpVersion) {
            generalItems.push('<li><strong>HTTP version:</strong> ' + entryComplete.request.httpVersion + '</li>');
        }
        if (entryComplete.request && entryComplete.request.method) {
            generalItems.push('<li><strong>Method:</strong> ' + entryComplete.request.method + '</li>');
        }
        if (entryComplete.serverIPAddress) {
            generalItems.push('<li><strong>Remote IP:</strong> ' + entryComplete.serverIPAddress + '</li>');
        }
        var generalHtml = '<div style="margin-bottom:10px;"><strong>General:</strong><ul style="list-style:none;margin:4px 0 0 0;">' + generalItems.join('') + '</ul></div>';
        this.request.innerHTML = generalHtml + '<p><strong>Headers</strong></p>';
        $('#' + this.details.id).append(this.request);

        this.requestList = document.createElement('ul');
        this.requestList.id = 'HarViewerDetailsRequestList';
        $('#HarViewerDetailsRequest').append(this.requestList);

        // Request headers
        if (entryComplete.request && entryComplete.request.headers) {
            var sortedHeaders = entryComplete.request.headers.slice().sort(function (a, b) {
                return a.name.localeCompare(b.name);
            });
            for (var i = 0; i < sortedHeaders.length; i++) {
                var header = sortedHeaders[i];
                var li = document.createElement('li');
                li.innerHTML = '<p><strong>' + header.name + '</strong>: ' + header.value + '</p>';
                $('#HarViewerDetailsRequestList').append(li);
            }
        }
        // Sección Cookies en Request
        renderCookiesSection(this.request.id, (entryComplete.request && entryComplete.request.cookies) ? entryComplete.request.cookies : [], 'HarViewerDetailsRequestCookies');

        // Sección para mostrar el contenido de la request
        var contentDiv = document.createElement('div');
        contentDiv.id = 'HarViewerDetailsRequestContent';
        contentDiv.innerHTML = '<p><strong>Content</strong></p>';
        var contentList = document.createElement('ul');
        contentList.id = 'HarViewerDetailsRequestContentList';
        var hasContent = false;
        if (entryComplete.request && entryComplete.request.postData) {
            var postData = entryComplete.request.postData;
            // Si hay params, los mostramos en formato lista
            if (Array.isArray(postData.params) && postData.params.length > 0) {
                hasContent = true;
                for (var i = 0; i < postData.params.length; i++) {
                    var param = postData.params[i];
                    var li = document.createElement('li');
                    li.innerHTML = '<strong>Name:</strong> ' + (param.name || '') +
                        ', <strong>Value:</strong> ' + (param.value || '') +
                        (param.comment ? ', <strong>Comment:</strong> ' + param.comment : '');
                    contentList.appendChild(li);
                }
            }
            // Si hay texto, lo mostramos debajo en un li
            if (postData.text) {
                hasContent = true;
                var li = document.createElement('li');
                li.innerHTML = '<pre style="white-space:pre-wrap;word-break:break-all;margin-top:8px;">' + postData.text + '</pre>';
                contentList.appendChild(li);
            }
        }
        if (!hasContent) {
            var li = document.createElement('li');
            li.innerHTML = '<span style="color:#888;">No content.</span>';
            contentList.appendChild(li);
        }
        contentDiv.appendChild(contentList);
        $('#' + this.request.id).append(contentDiv);
    }

    this.initResponse = function () {
        this.response = document.createElement('div');
        this.response.id = 'HarViewerDetailsResponse';
        // General section for response
        var entry = this.entriesSummaries[this.currentEntryFocused];
        var entryComplete = this.entries[this.currentEntryFocused];
        var generalItems = [];
        if (entryComplete.response && entryComplete.response.status && entryComplete.response.statusText && entryComplete.response.httpVersion) {
            generalItems.push('<li>' + entryComplete.response.status + ' ' + entryComplete.response.statusText + ' ' + entryComplete.response.httpVersion + '</li>');
        } else if (entryComplete.response && entryComplete.response.status && entryComplete.response.httpVersion) {
            generalItems.push('<li>' + entryComplete.response.status + ' ' + entryComplete.response.httpVersion + '</li>');
        }
        var generalHtml = '<div style="margin-bottom:10px;"><strong>General:</strong><ul style="list-style:none;margin:4px 0 0 0;">' + generalItems.join('') + '</ul></div>';
        this.response.innerHTML = generalHtml + '<p><strong>Headers</strong></p>';
        $('#' + this.details.id).append(this.response);
        this.responseList = document.createElement('ul');
        this.responseList.id = 'HarViewerDetailsResponseList';
        $('#HarViewerDetailsResponse').append(this.responseList);

        var entry = this.entriesSummaries[this.currentEntryFocused];
        var entryComplete = this.entries[this.currentEntryFocused];

        // Response headers
        if (entryComplete.response && entryComplete.response.headers) {
            var sortedHeaders = entryComplete.response.headers.slice().sort(function (a, b) {
                return a.name.localeCompare(b.name);
            });
            for (var i = 0; i < sortedHeaders.length; i++) {
                var header = sortedHeaders[i];
                var li = document.createElement('li');
                li.innerHTML = '<p><strong>' + header.name + '</strong>: ' + header.value + '</p>';
                $('#HarViewerDetailsResponseList').append(li);
            }
        }
        // Sección Cookies en Response
        renderCookiesSection(this.response.id, (entryComplete.response && entryComplete.response.cookies) ? entryComplete.response.cookies : [], 'HarViewerDetailsResponseCookies');

        // Sección para mostrar el contenido de la response
        var contentDiv = document.createElement('div');
        contentDiv.id = 'HarViewerDetailsResponseContent';
        contentDiv.innerHTML = '<p><strong>Content</strong></p>';
        var contentList = document.createElement('ul');
        contentList.id = 'HarViewerDetailsResponseContentList';
        var hasContent = false;
        if (entryComplete.response && entryComplete.response.content && entryComplete.response.content.text) {
            hasContent = true;
            var mimeType = entryComplete.response.content.mimeType || '';
            var li = document.createElement('li');
            // Mostrar imagen si es tipo imagen
            if (/^image\/(jpeg|png|bmp|gif|webp|svg\+xml|x-icon|vnd\.microsoft\.icon)/.test(mimeType)) {
                // Si el contenido está en base64 (HAR suele usar encoding base64)
                var encoding = entryComplete.response.content.encoding || '';
                var src = '';
                if (encoding === 'base64') {
                    src = 'data:' + mimeType + ';base64,' + entryComplete.response.content.text;
                } else {
                    // Si no está en base64, intentar mostrar como texto plano (no recomendado para imágenes)
                    src = 'data:' + mimeType + ',' + encodeURIComponent(entryComplete.response.content.text);
                }
                li.innerHTML = '<img src="' + src + '" style="max-width:100%;max-height:300px;display:block;margin:8px auto;" alt="Image response"/>';
            } else if (mimeType.indexOf('html') !== -1) {
                // Si es HTML, mostrar enlace de descarga
                var blob = new Blob([entryComplete.response.content.text], { type: 'text/html' });
                var url = URL.createObjectURL(blob);
                li.innerHTML = '<a href="' + url + '" download="response.html">Download HTML</a>';
            } else if (mimeType.indexOf('css') !== -1) {
                // Si es CSS, mostrar enlace de descarga
                var blob = new Blob([entryComplete.response.content.text], { type: 'text/css' });
                var url = URL.createObjectURL(blob);
                li.innerHTML = '<a href="' + url + '" download="response.css">Download CSS</a>';
            } else if (mimeType.indexOf('javascript') !== -1) {
                // Si es JavaScript, mostrar enlace de descarga
                var blob = new Blob([entryComplete.response.content.text], { type: 'application/javascript' });
                var url = URL.createObjectURL(blob);
                li.innerHTML = '<a href="' + url + '" download="response.js">Download Javascript</a>';
            } else if (mimeType.indexOf('json') !== -1) {
                // Si es Json, mostrar el contenido y enlace de descarga
                var blob = new Blob([entryComplete.response.content.text], { type: 'application/json' });
                var url = URL.createObjectURL(blob);
                li.innerHTML = '<pre style="white-space:pre-wrap;word-break:break-all;">' 
                    + entryComplete.response.content.text 
                    + '</pre>'
                    + '<br/>'
                    + '<a href="' + url + '" download="response.json">Download Json</a>';
            } else {
                // Si no es HTML, CSS o JS, mostrar el contenido normalmente
                li.innerHTML = '<pre style="white-space:pre-wrap;word-break:break-all;">' + entryComplete.response.content.text + '</pre>';
            }
            contentList.appendChild(li);
        }
        if (!hasContent) {
            var li = document.createElement('li');
            li.innerHTML = '<span style="color:#888;">No content.</span>';
            contentList.appendChild(li);
        }
        contentDiv.appendChild(contentList);
        $('#' + this.response.id).append(contentDiv);

    }

    this.initTimings = function () {
        this.timings = document.createElement('div');
        this.timings.id = 'HarViewerDetailsTimings';
        this.timings.innerHTML = '<p><strong>Timings</strong></p>';
        $('#' + this.details.id).append(this.timings);
        this.timingsList = document.createElement('ul');
        this.timingsList.id = 'HarViewerDetailsTimingsList';
        $('#HarViewerDetailsTimings').append(this.timingsList);

        var entry = this.entriesSummaries[this.currentEntryFocused];

        var entryTimingsList = entry.Timings

        for (i = 0; i < this.entryTimings.length; i++) {

            var li = document.createElement('li');
            li.innerHTML = '<p><strong>' + this.entryTimings[i] + '</strong>: ' + entryTimingsList[this.entryTimings[i]] + '</p>';
            $('#HarViewerDetailsTimingsList').append(li);


        }
    }

    this.initDetails = function () {
        this.details = document.createElement('div');
        this.details.id = 'HarViewerDetails';

        if ($('#' + this.details.id).length) {
            $('#' + this.details.id).remove();
        }

        $('#' + this.right.id).append(this.details);

        // Selectores Response/Request (Response primero)
        var selectorBar = document.createElement('div');
        selectorBar.id = 'HarViewerDetailsSelectorBar';

        selectorBar.style.display = 'flex';
        selectorBar.style.gap = '0px';
        selectorBar.style.marginBottom = '12px';
        selectorBar.style.borderBottom = '2px solid #c2c2c2';

        var responseSelector = document.createElement('span');
        responseSelector.id = 'HarViewerDetailsSelectorResponse';
        responseSelector.textContent = 'Response';
        responseSelector.className = 'HarViewerTabSelector active';

        var requestSelector = document.createElement('span');
        requestSelector.id = 'HarViewerDetailsSelectorRequest';
        requestSelector.textContent = 'Request';
        requestSelector.className = 'HarViewerTabSelector';

        selectorBar.appendChild(responseSelector);
        selectorBar.appendChild(requestSelector);
        this.details.appendChild(selectorBar);

        // Secciones
        this.initRequest();
        this.initResponse();
        // Mostrar response por defecto, ocultar request
        $('#' + this.request.id).hide();
        $('#' + this.response.id).show();

        // Eventos de los selectores
        requestSelector.addEventListener('click', function() {
            $('#' + viewer.request.id).show();
            $('#' + viewer.response.id).hide();
            requestSelector.classList.add('active');
            responseSelector.classList.remove('active');
        });
        responseSelector.addEventListener('click', function() {
            $('#' + viewer.request.id).hide();
            $('#' + viewer.response.id).show();
            requestSelector.classList.remove('active');
            responseSelector.classList.add('active');
        });

        this.initTimings();

        $('#' + this.details.id).outerHeight($('#' + this.table.id).outerHeight(true));

        // Ocultar detalles si no hay ninguna fila seleccionada
        if (this.currentEntryFocused === null) {
            $('#' + this.details.id).hide();
        } else {
            $('#' + this.details.id).show();
        }
    }


    this.showDetails = function () {
        var index = this.currentEntryFocused;
        var selectedColor = '#e0f3ff';
        // Buscar la fila visible (filtrada o no) que corresponde al índice real
        var row = document.querySelector('[data-real-index="' + index + '"]');
        if (row) {
            row.style.backgroundColor = selectedColor;
        }
        var fieldsToBold = ['Hour', 'URL', 'Method', 'Status', 'Type'];
        fieldsToBold.forEach(function (field) {
            var cell = document.querySelector('[id^="HarViewerRow_' + field + '_"][data-real-index="' + index + '"]');
            if (cell) {
                var p = cell.querySelector('p');
                if (p) {
                    p.style.fontWeight = 'bold';
                }
            }
        });
        // También cambiar el color en la waterfall si existe
        var wfRow = document.getElementById('HarViewerWaterFallRow_' + index);
        if (wfRow) {
            wfRow.style.backgroundColor = selectedColor;
        }
        $('#' + this.details.id).css('display', 'block');
        // Mostrar la pestaña Response por defecto
        if (this.response && this.request) {
            $('#' + this.request.id).hide();
            $('#' + this.response.id).show();
            var requestSelector = document.getElementById('HarViewerDetailsSelectorRequest');
            var responseSelector = document.getElementById('HarViewerDetailsSelectorResponse');
            if (requestSelector && responseSelector) {
                requestSelector.classList.remove('active');
                responseSelector.classList.add('active');
            }
        }
        // Ajustar el alto de HarViewerDetails para que iguale al de HarViewerContent
        var contentDiv = document.getElementById('HarViewerContent');
        var detailsDiv = document.getElementById(this.details.id);
        if (contentDiv && detailsDiv) {
            detailsDiv.style.height = contentDiv.offsetHeight + 'px';
            detailsDiv.style.overflowY = 'auto';
            detailsDiv.style.boxSizing = 'border-box';
        }
    }

    this.showWaterFall = function () {
        $('#' + this.waterfall.id).css('display', 'block');
    }

    this.hideDetails = function () {
        var index = this.currentEntryFocused;
        // Restaurar color y negrita en la fila visible (filtrada o no)
        var index = this.currentEntryFocused;
        var row = document.querySelector('[data-real-index="' + index + '"]');
        if (row) {
            var color = (index % 2 == 0) ? this.colorPairRow : this.colorUnpairRow;
            row.style.backgroundColor = color;
        }
        var fieldsToBold = ['Hour', 'URL', 'Method', 'Status', 'Type'];
        fieldsToBold.forEach(function (field) {
            var cell = document.querySelector('[id^="HarViewerRow_' + field + '_"][data-real-index="' + index + '"]');
            if (cell) {
                var p = cell.querySelector('p');
                if (p) {
                    p.style.fontWeight = '';
                }
            }
        });
        // También restaurar color en la waterfall si existe
        var wfRow = document.getElementById('HarViewerWaterFallRow_' + index);
        if (wfRow) {
            var color = (index % 2 == 0) ? this.colorPairRow : this.colorUnpairRow;
            wfRow.style.backgroundColor = color;
        }
        if (viewer.currentEntryFocused % 2 == 0) {
            $('#HarViewerWaterFallRow_' + viewer.currentEntryFocused).css('background-color', this.colorPairRow);
            $('#HarViewerRow_' + viewer.currentEntryFocused).css('background-color', this.colorPairRow);
        } else {
            $('#HarViewerWaterFallRow_' + viewer.currentEntryFocused).css('background-color', this.colorUnpairRow);
            $('#HarViewerRow_' + viewer.currentEntryFocused).css('background-color', this.colorUnpairRow);
        }
        $('#' + this.details.id).css('display', 'none');
    }

    this.hideWaterFall = function () {
        $('#' + this.waterfall.id).css('display', 'none');
    }

    this.onClickOnSearch = function (event) {
        $('#' + viewer.search.id).prop('value', '');
    }

    this.onFocusOutSearch = function (event) {
        $('#' + viewer.search.id).prop('value', 'Search...');
    }

    this.onSearch = function (event) {
        var str = $('#' + viewer.search.id).prop('value').toLowerCase();
        viewer.showedEntries = [];
        for (var i = 0; i < viewer.entriesSummaries.length; i++) {
            var entry = viewer.entriesSummaries[i];
            var nameMatch = entry.Name && entry.Name.toString().toLowerCase().includes(str);
            var urlMatch = entry.URL && entry.URL.toString().toLowerCase().includes(str);
            var hourMatch = false;
            if (entry.date) {
                var d = new Date(entry.date);
                var hh = String(d.getHours()).padStart(2, '0');
                var mm = String(d.getMinutes()).padStart(2, '0');
                var ss = String(d.getSeconds()).padStart(2, '0');
                var hourStr = hh + ':' + mm + ':' + ss;
                hourMatch = hourStr.includes(str);
            }
            var methodMatch = entry.Method && entry.Method.toString().toLowerCase().includes(str);
            var statusMatch = entry.Status && entry.Status.toString().toLowerCase().includes(str);
            var typeMatch = entry.Type && entry.Type.toString().toLowerCase().includes(str);
            if (nameMatch || urlMatch || hourMatch || methodMatch || statusMatch || typeMatch) {
                viewer.showedEntries.push(entry);
            }
        }
        // Limpiar la tabla antes de renderizar resultados
        var table = document.getElementById('HarViewerTable');
        if (table) {
            table.innerHTML = '';
        }
        // Si no hay resultados, mostrar la tabla vacía y ocultar waterfall y detalles
        if (viewer.showedEntries.length === 0 && table) {
            table.innerHTML = '<div style="padding:16px;color:#888;text-align:left;">No results found.</div>';
            // Limpiar waterfall
            var waterfall = document.getElementById('HarViewerWaterFall');
            if (waterfall) {
                waterfall.innerHTML = '';
            }
            // Ocultar panel de detalles si está visible
            var details = document.getElementById('HarViewerDetails');
            if (details) {
                details.style.display = 'none';
            }
            return;
        }
        viewer.initTime();
        viewer.initTable();
        viewer.initWaterFallView();
        // Tras filtrar, nunca seleccionar ninguna fila automáticamente
        viewer.currentEntryFocused = null;
        var details = document.getElementById('HarViewerDetails');
        if (details) {
            details.style.display = 'none';
        }
        // Asegurar que los detalles están ocultos si no hay selección
        if (viewer.currentEntryFocused === null && details) {
            details.style.display = 'none';
        }

    }

    this.onEntry = function (event) {
        var realIndex = event.currentTarget.dataset.realIndex;
        var index = parseInt(realIndex);
        if (index == viewer.currentEntryFocused && !viewer.firstDetails) {
            viewer.hideDetails();
            viewer.showWaterFall();
            viewer.currentEntryFocused = null;
            if (index % 2 == 0) {
                $('#HarViewerWaterFallRow_' + index).css('background-color', '#FFFFFF');
                $('#HarViewerRow_' + index).css('background-color', '#FFFFFF');
            } else {
                $('#HarViewerWaterFallRow_' + index).css('background-color', '#F2F2F2');
                $('#HarViewerRow_' + index).css('background-color', '#F2F2F2');
            }
        } else if (viewer.firstDetails) {
            viewer.firstDetails = false;
            viewer.currentEntryFocused = index;
            viewer.initDetails();
            viewer.hideWaterFall();
            viewer.showDetails();
        } else {
            // Quitar negrita y color de la fila previamente seleccionada (filtrada o no)
            var prevIndex = viewer.currentEntryFocused;
            var prevRow = document.querySelector('[data-real-index="' + prevIndex + '"]');
            if (prevRow) {
                var color = (prevIndex % 2 == 0) ? viewer.colorPairRow : viewer.colorUnpairRow;
                prevRow.style.backgroundColor = color;
            }
            var fieldsToBold = ['Hour', 'URL', 'Method', 'Status', 'Type'];
            fieldsToBold.forEach(function (field) {
                var prevCell = document.querySelector('[id^="HarViewerRow_' + field + '_"][data-real-index="' + prevIndex + '"]');
                if (prevCell) {
                    var prevP = prevCell.querySelector('p');
                    if (prevP) {
                        prevP.style.fontWeight = '';
                    }
                }
            });
            var prevWfRow = document.getElementById('HarViewerWaterFallRow_' + prevIndex);
            if (prevWfRow) {
                var color = (prevIndex % 2 == 0) ? viewer.colorPairRow : viewer.colorUnpairRow;
                prevWfRow.style.backgroundColor = color;
            }
            viewer.currentEntryFocused = index;
            viewer.initDetails();
            viewer.hideWaterFall();
            viewer.showDetails();
        }
    }

    this.onFilter = function (event) {

        var id = event.currentTarget.id;
        var c = 0;
        var find = false;
        for (var j = id.length - 1; (j >= 0 && !find); j--) {
            if (id[j] == '_') {
                c = j;
                find = true;
            }
        }
        var no = id.substring((c + 1));
        var column = viewer.listFilters[no];
        // Toggle sort direction if clicking same column
        if (viewer.lastSortColumn === column) {
            viewer.lastSortDirection *= -1;
        } else {
            viewer.lastSortDirection = 1;
        }
        viewer.lastSortColumn = column;
        viewer.showedEntries.sort(function (a, b) {
            return viewer.compareTwoEntries(a, b, column) * viewer.lastSortDirection;
        });
        viewer.initTime();
        viewer.initTable();
        viewer.initWaterFallView();



    }

    this.compareTwoEntries = function (a, b, attr) {
        if (attr === 'Hour') {
            // Comparar por el campo date
            if (a.date > b.date) return 1;
            if (a.date < b.date) return -1;
            return 0;
        }
        var ret = 0;
        if (a[attr] > b[attr]) {
            ret = 1;
        } else if (a[attr] < b[attr]) {
            ret = -1;
        }
        return ret;
    }

    $(window).resize(function () {
        viewer.initFrontEnd();
        viewer.initIndicators();
    });

}

function parseHar(har) {

    var entries = har.log.entries;
    entries.sort(compareEntries);

    return entries;
}

function compareEntries(a, b) {

    dateA = new Date(a.startedDateTime)
    dateB = new Date(b.startedDateTime)
    return dateA.valueOf() - dateB.valueOf();

}
