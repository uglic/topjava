// $(document).ready(function () {
$(function () {
    makeEditable({
            ajaxUrl: "ajax/admin/users/",
            datatableApi: $("#datatable").DataTable({
                "paging": false,
                "info": true,
                "columns": [
                    {
                        "data": "name"
                    },
                    {
                        "data": "email"
                    },
                    {
                        "data": "roles"
                    },
                    {
                        "data": "enabled"
                    },
                    {
                        "data": "registered"
                    },
                    {
                        "defaultContent": "Edit",
                        "orderable": false
                    },
                    {
                        "defaultContent": "Delete",
                        "orderable": false
                    }
                ],
                "order": [
                    [
                        0,
                        "asc"
                    ]
                ]
            }),
            onAfterDeleteCallback: updateUsers,
            onAfterSaveCallback: updateUsers
        }
    );
    initUserHandlers();
    updateUserTableStyles();
});

function initUserHandlers() {
    $("#datatable tr > input,[type='checkbox'][id^='enabled.']")
        .change(function (e) {
            let check = this;
            let row = $(this).closest("tr");
            let id = row.attr("id");
            $.ajax({
                type: "POST",
                url: context.ajaxUrl + id + "/enable",
                data: "enabled=" + check.checked
            }).done(function () {
                let username = $(row).children("td[id^='name.']")[0].innerText;
                successNoty("User [" + username + "] " + (check.checked ? "enabled" : "disabled"));
            }).fail(function () {
                $(check).prop("checked", !check.checked);
            }).always(function () {
                if (check.checked) {
                    row.removeClass("table-warning");
                } else {
                    row.addClass("table-warning");
                }
            });
        });
}

function updateUserTableStyles() {
    $("#datatable tr[data-userEnabled='false']").addClass("table-warning");
    $("#datatable tr[data-userEnabled='true']").removeClass("table-warning");
}

function updateUsers() {
    $.get(context.ajaxUrl, updateData);
}
