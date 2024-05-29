// 로그인 확인
function validUserLogin() {
    let access = getAccessToken();
    const refresh = getRefreshToken();

    $.ajax({
        type: 'GET',
        url: '/api/user/token-validation',
        contentType: 'application/json',
        data: {"accessToken": access},
        success: function (response) {
            if (!response.status) {
                $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
                    jqXHR.setRequestHeader('X-AUTH-REFRESH-TOKEN', refresh);
                });
            }else {
                $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
                    jqXHR.setRequestHeader('X-AUTH-ACCESS-TOKEN', access);
                });
            }
        },error: err => {
            alert("로그인이 필요한 서비스입니다.")
            window.location.href = host + '/api/user/login-page';
        }
    })
}

// 회원 일정
function userSchedules() {

    validUserLogin();

    $.ajax({
        type: 'GET',
        url: `/api/user-info`,
        contentType: 'application/json',
    })
        .done(function (res, status, xhr) {
            const username = res.username;
            const isAdmin = !!res.admin;

            if (!username) {
                alert("로그인이 필요한 서비스입니다.")
                window.location.href = '/api/user/login-page';
                return;
            }

            $('#username').text(username);
            if (isAdmin) {
                $('#admin').text(true);
                showschedule();
            } else {
                showschedule();
            }

            // 로그인한 유저의 폴더
            $.ajax({
                type: 'GET',
                url: `/api/user-folder`,
                error(error) {
                    logout();
                }
            }).done(function (fragment) {
                $('#fragment').replaceWith(fragment);
            });

        })
        .fail(function (jqXHR, textStatus) {
            logout();
        });
}

// 일정 추가 팝업
function openAddSchedulePopup() {
    validUserLogin();
    $('#container3').addClass('active');
}

// 일정 수정 팝업
function openModSchedulePopup(id, title, content) {

    validUserLogin();

    $('#container4').addClass('active');

    document.getElementById("scheduleId").value = id;
    document.getElementById("modTitle").value = title;
    document.getElementById("modContent").value = content;
}

// 일정 등록
function addSchedule() {

    validUserLogin();

    const title = $('#title').val()
    const content = $('#content').val()

    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);

    const fileCount = $("input[name='files']").length;

    if (fileCount > 0) {
        for (var i=0; i<fileCount; i++) {
            if ($("input[name='files']")[i].files[0] != null) {
                formData.append("files", $("input[name='files']")[i].files[0]);
            }
        }
    }

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
            alert(err.responseJSON.message);
        }
    });
}

// 일정 삭제
function deleteSchedule(id) {
    validUserLogin();
    $.ajax({
        type: "DELETE",
        url: `/api/schedules/${id}`,
        success: function (response) {
            alert('일정이 성공적으로 삭제되었습니다.');
            window.location.reload();
        },error: err => {
            alert(err.responseJSON.message);
        }
    });
}

// 사용자 일정 목록 조회
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
        dataSource = `/api/schedules?sortBy=${sorting}&isAsc=${isAsc}&folderId=${folderId}`;
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
                $('#schedule-container').html('일정이 없습니다.');
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
                let tempHtml = addUserScheduleItem(schedule, "show");
                $('#schedule-container').append(tempHtml);
            }
        }
    });
}

// 사용자 일정 HTML 생성
function addUserScheduleItem(schedule, type) {
    let folders = schedule.scheduleFolderList.map(folder =>
            `
            <span onclick="openFolder(${folder.id})">
                #${folder.name}
            </span>
        `
        );

    let tempHTML = `<div class="schedule-card">
                    <div class="card-body">
                        <div id="user-title-${schedule.id}" class="title">
                            ${schedule.title}
                        </div>
                        <div class="writer">
                            <span>담당자 : </span>${schedule.writer}
                        </div>
                        <div class="date">
                            <span>수정 : </span>${schedule.modifiedAt}
                        </div>
                        <div id="user-content-${schedule.id}" class="content">
                            <span>${schedule.content}</span>
                        </div>
                        <div id="${schedule.id}-user-files-box">
                        </div>
                        <button type="button" class="schedule-btn" onclick="openModSchedulePopup(${schedule.id}, '${schedule.title}', '${schedule.content}')">일정 수정</button>
                        <button type="button" class="schedule-btn" onclick="deleteSchedule(${schedule.id})">일정 삭제</button>
                        <button type="button" class="schedule-btn" onclick="getFileList(${schedule.id})">첨부파일 보기</button>
                        <button type="button" class="schedule-btn" onclick="getCommentList(${schedule.id}, 'user')">댓글 보기</button>
                    </div>
                    <div class="schedule-tags" style="margin-bottom: 20px;">
                        ${folders}
                        <span onclick="addInputForScheduleToFolder(${schedule.id}, this)">
                            <svg xmlns="http://www.w3.org/2000/svg" width="30px" fill="currentColor" class="bi bi-folder-plus" viewBox="0 0 16 16">
                                <path d="M.5 3l.04.87a1.99 1.99 0 0 0-.342 1.311l.637 7A2 2 0 0 0 2.826 14H9v-1H2.826a1 1 0 0 1-.995-.91l-.637-7A1 1 0 0 1 2.19 4h11.62a1 1 0 0 1 .996 1.09L14.54 8h1.005l.256-2.819A2 2 0 0 0 13.81 3H9.828a2 2 0 0 1-1.414-.586l-.828-.828A2 2 0 0 0 6.172 1H2.5a2 2 0 0 0-2 2zm5.672-1a1 1 0 0 1 .707.293L7.586 3H2.19c-.24 0-.47.042-.684.12L1.5 2.98a1 1 0 0 1 1-.98h3.672z"/>
                                <path d="M13.5 10a.5.5 0 0 1 .5.5V12h1.5a.5.5 0 0 1 0 1H14v1.5a.5.5 0 0 1-1 0V13h-1.5a.5.5 0 0 1 0-1H13v-1.5a.5.5 0 0 1 .5-.5z"/>
                            </svg>
                        </span>
                    </div>
                    <div id="${schedule.id}-user-comments-box">
                    </div>
            </div>`;

    return tempHTML;
}

// 전체 일정 HTML 생성
function addAllScheduleItem(schedule, type) {
    let tempHTML = `<div class="schedule-card">
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
                        <div id="${schedule.id}-all-files-box">
                        </div>
                        <button type="button" class="schedule-btn" onclick="openModSchedulePopup(${schedule.id}, '${schedule.title}', '${schedule.content}')">일정 수정</button>
                        <button type="button" class="schedule-btn" onclick="deleteSchedule(${schedule.id})">일정 삭제</button>
                        <button type="button" class="schedule-btn" onclick="getFileList(${schedule.id})">첨부파일 보기</button>
                        <button type="button" class="schedule-btn" onclick="getCommentList(${schedule.id}, 'all')">댓글 보기</button>
                    </div>
                    <div id="${schedule.id}-all-comments-box">
                    </div>
            </div>`;

    return tempHTML;
}

// 검색 일정 HTML 생성
function addSearchScheduleItem(schedule, type) {
    let tempHTML = `<div class="schedule-card">
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
                        <div id="${schedule.id}-search-files-box">
                        </div>
                        <button type="button" class="schedule-btn" onclick="openModSchedulePopup(${schedule.id}, '${schedule.title}', '${schedule.content}')">일정 수정</button>
                        <button type="button" class="schedule-btn" onclick="deleteSchedule(${schedule.id})">일정 삭제</button>
                        <button type="button" class="schedule-btn" onclick="getFileList(${schedule.id})">첨부파일 보기</button>
                        <button type="button" class="schedule-btn" onclick="getCommentList(${schedule.id}, 'search')">댓글 보기</button>
                    </div>
                    <div id="${schedule.id}-search-comments-box">
                    </div>
            </div>`;

    return tempHTML;
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
                let tempHtml = addSearchScheduleItem(schedule);
                $('#search-result-box').append(tempHtml);
            }
        }
    });
}

// 전체 일정 목록 조회
function showAllschedule() {

    var sorting = $("#see-sorting option:selected").val();
    var isAsc = $(':radio[name="see-isAsc"]:checked').val();

    let dataSource = `/api/schedules/all?sortBy=${sorting}&isAsc=${isAsc}`;

    $('#see-result-box').empty();
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
            beforeSend: function () {
                $('#see-result-box').html('일정이 없습니다.');
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
            $('#see-result-box').empty();
            for (let i = 0; i < response.length; i++) {
                let schedule = response[i];
                let tempHtml = addAllScheduleItem(schedule);
                $('#see-result-box').append(tempHtml);
            }
        }
    });
}