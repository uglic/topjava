const mealsAjaxUrl = "ajax/profile/meals/";

function updateFilteredTable() {
    $.ajax({
        type: "GET",
        url: mealsAjaxUrl + "filter",
        data: $("#filter").serialize()
    }).done(updateTableByData);
}

function clearFilter() {
    $("#filter")[0].reset();
    $.get("ajax/profile/meals/", updateTableByData);
}

$(function () {
    makeEditable({
        ajaxUrl: mealsAjaxUrl,
        datatableApi: $("#datatable").DataTable({
            "ajax": {
                "url": mealsAjaxUrl,
                "dataSrc": ""
            },
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime",
                    "render": function (data, type, row) {
                        if (type === "display") {
                            return renderDateInput(data);
                        }
                        return data;
                    }
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false,
                    "render": renderEditBtn
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false,
                    "render": renderDeleteBtn
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ],
            "createdRow": function (row, data, dataIndex) {
                $(row).attr("data-mealExcess", data.excess);
            }
        }),
        updateTable: updateFilteredTable,
        renderValueForKey: renderValueForKey
    });
    setDateTimePickers();
});

function setDateTimePickers() {
    $.datetimepicker.setLocale(getLocale());


    $("#dateTime").datetimepicker({format: 'Y-m-d H:i'});
    let dateFormat = {timepicker: false, format: 'Y-m-d'};
    let timeFormat = {datepicker: false, format: 'H:i'};
    let startDate = $("#startDate");
    let endDate = $("#endDate");
    let startTime = $("#startTime");
    let endTime = $("#endTime");
    startDate.datetimepicker(dateFormat);
    endDate.datetimepicker(dateFormat);
    startTime.datetimepicker(timeFormat);
    endTime.datetimepicker(timeFormat);
    startDate.change(function () {
        endDate.datetimepicker({timepicker: false, format: dateFormat.format, minDate: $(this).val()});
    });
    endDate.change(function () {
        startDate.datetimepicker({timepicker: false, format: dateFormat.format, maxDate: $(this).val()});
    });
    startTime.change(function () {
        endTime.datetimepicker({datepicker: false, format: timeFormat.format, minTime: $(this).val()});
    });
    endTime.change(function () {
        startTime.datetimepicker({datepicker: false, format: timeFormat.format, maxTime: $(this).val()});
    });
}

function getLocale() {
    if (navigator.languages !== undefined)
        return navigator.languages[0].substring(0, 2);
    else
        return navigator.language.substring(0, 2);
}

function renderDateInput(value) {
    return value.substr(0, 16).replace("T", " ");
}

function renderValueForKey(key, value) {
    switch (key) {
        case "dateTime":
            return renderDateInput(value);
        default:
            return value
    }
}