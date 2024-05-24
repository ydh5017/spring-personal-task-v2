// 일정 추가 팝업
function openAddSchedulePopup() {
    $('#container3').addClass('active');
}

// 일정 수정 팝업
function openModSchedulePopup(id, title, content) {
    $('#container4').addClass('active');

    document.getElementById("scheduleId").value = id;
    document.getElementById("modTitle").value = title;
    document.getElementById("modContent").value = content;
}

// 일정 추가
function addSchedule() {
    const title = $('#title').val()
    const content = $('#content').val()

    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);

    $.ajax({
        type: "POST",
        url: "/api/schedules",
        contentType: false,
        processData: false,
        data: formData
    }).done(function (data, textStatus, xhr) {
        $('#container3').removeClass('active');
        alert('일정이 성공적으로 등록되었습니다.');
        window.location.reload();
    })
        .fail(function(xhr, textStatus, errorThrown) {
            alert("일정 등록 실패");
        });
}

// 일정 수정
function ModSchedule() {
    const id = $('#scheduleId').val()
    const title = $('#modTitle').val()
    const content = $('#modContent').val()

    const data = {'title': title, 'content': content};

    $.ajax({
        type: "PUT",
        url: `/api/schedules/${id}`,
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (response) {
            $('#container3').removeClass('active');
            alert('일정이 성공적으로 수정되었습니다.');
            window.location.reload();
        },error: err => {
            // alert(err.responseJSON.message);
            alert("일정 수정 실패");
        }
    });
}

// 일정 삭제
function deleteSchedule(id) {
    $.ajax({
        type: "DELETE",
        url: `/api/schedules/${id}`,
        success: function (response) {
            alert('일정이 성공적으로 삭제되었습니다.');
            window.location.reload();
        },error: err => {
            // alert(err.responseJSON.message);
            alert("일정 수정 실패");
        }
    });
}

// 일정 목록 조회
function showschedule(folderId = null) {
    /**
     * 일정 목록: #schedule-container
     * 검색결과 목록: #search-result-box
     * 관심상품 HTML 만드는 함수: addProductItem
     */

    let dataSource = null;

    var sorting = $("#sorting option:selected").val();
    var isAsc = $(':radio[name="isAsc"]:checked').val();

    if (folderId) {
        dataSource = `/api/folders/${folderId}/schedules?sortBy=${sorting}&isAsc=${isAsc}`;
    } else if(folderTargetId === undefined) {
        dataSource = `/api/schedules?sortBy=${sorting}&isAsc=${isAsc}`;
    } else {
        dataSource = `/api/folders/${folderTargetId}/schedules?sortBy=${sorting}&isAsc=${isAsc}`;
    }

    $('#schedule-container').empty();
    $('#search-result-box').empty();
    $('#pagination').pagination({
        dataSource,
        locator: 'content',
        alias: {
            pageNumber: 'page',
            pageSize: 'size'
        },
        totalNumberLocator: (response) => {
            return response.totalElements;
        },
        pageSize: 10,
        showPrevious: true,
        showNext: true,
        ajax: {
            beforeSend: function () {
                $('#schedule-container').html('상품 불러오는 중...');
            },
            error(error, status, request) {
                if (error.status === 403) {
                    $('html').html(error.responseText);
                    return;
                }
                logout();
            }
        },
        callback: function (response, pagination) {
            $('#schedule-container').empty();
            for (let i = 0; i < response.length; i++) {
                let schedule = response[i];
                let tempHtml = addScheduleItem(schedule);
                $('#schedule-container').append(tempHtml);
            }
        }
    });
}

// 일정 HTML 생성
function addScheduleItem(schedule) {
    // const folders = schedule.scheduleFolderList.map(folder =>
    //     `
    //         <span onclick="openFolder(${folder.id})">
    //             #${folder.name}
    //         </span>
    //     `
    // );

    return `<div class="schedule-card">
                    <div class="card-body">
                        <div id="title-${schedule.id}" class="title">
                            ${schedule.title}
                        </div>
                        <div class="writer">
                            <span>담당자 : </span>${schedule.writer}
                        </div>
                        <div class="date">
                            <span>수정 : </span>${schedule.modifiedAt}
                        </div>
                        <div id="content-${schedule.id}" class="content">
                            <span>${schedule.content}</span>
                        </div>
                        <button type="button" class="schedule-btn" onclick="openModSchedulePopup(${schedule.id}, '${schedule.title}', '${schedule.content}')">일정 수정</button>
                        <button type="button" class="schedule-btn" onclick="deleteSchedule(${schedule.id})">일정 삭제</button>
                    </div>
            </div>`;
}

// 일정 검색 조회
function execSearch() {
    /**
     * 검색어 input id: query
     * 검색결과 목록: #search-result-box
     * 검색결과 HTML 만드는 함수: addHTML
     */
        // 1. 검색창의 입력값을 가져온다.
    let query = $('#query').val();

    // 2. 검색창 입력값을 검사하고, 입력하지 않았을 경우 focus.
    if (query == '') {
        alert('검색어를 입력해주세요');
        $('#query').focus();
        return;
    }

    var sorting = $("#search-sorting option:selected").val();
    var isAsc = $(':radio[name="search-isAsc"]:checked').val();

    let dataSource = `/api/schedules/search?sortBy=${sorting}&isAsc=${isAsc}&query=${query}`;

    $('#search-result-box').empty();
    // 3. GET /api/search?query=${query} 요청
    $('#search-pagination').pagination({
        dataSource,
        locator: 'content',
        alias: {
            pageNumber: 'page',
            pageSize: 'size'
        },
        totalNumberLocator: (response) => {
            return response.totalElements;
        },
        pageSize: 10,
        showPrevious: true,
        showNext: true,
        ajax: {
            beforeSend: function (response) {
                $('#search-result-box').html('검색어를 입력해주세요.');
            },
            error(error, status, request) {
                if (error.status === 403) {
                    $('html').html(error.responseText);
                    return;
                }
                logout();
            }
        },
        callback: function (response, pagination) {
            $('#search-result-box').empty();
            if (response.length <= 0) {
                $('#search-result-box').html('검색어에 해당 하는 일정이 없습니다.');
            }
            for (let i = 0; i < response.length; i++) {
                let schedule = response[i];
                let tempHtml = addScheduleItem(schedule);
                $('#search-result-box').append(tempHtml);
            }
        }
    });

}