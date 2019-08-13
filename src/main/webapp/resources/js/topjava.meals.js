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
                            return data.substr(0, 16).replace("T", " ");
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
        updateTable: updateFilteredTable
    });
    setDateTimePickers();
});

function setDateTimePickers() {
    $.datetimepicker.setLocale(getLocale());
    let dateFormat = {timepicker: false, format: 'Y-m-d'};
    let timeFormat = {datepicker: false, format: 'H:i'};
    $("#startDate").datetimepicker(dateFormat);
    $("#endDate").datetimepicker(dateFormat);
    $("#startTime").datetimepicker(timeFormat);
    $("#endTime").datetimepicker(timeFormat);
    $("#dateTime").datetimepicker({format: 'Y-m-d H:i'});
}

function getLocale() {
    if (navigator.languages !== undefined)
        return navigator.languages[0].substring(0, 2);
    else
        return navigator.language.substring(0, 2);
}