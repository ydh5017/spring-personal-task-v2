const host = 'http://' + window.location.host;
let targetId;
let folderTargetId;

$(document).ready(function () {
    const auth = getToken();

    if (auth !== undefined && auth !== '') {
        $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
            jqXHR.setRequestHeader('Authorization', auth);
        });
    } else {
        window.location.href = host + '/api/user/login-page';
        return;
    }

    $.ajax({
        type: 'GET',
        url: `/api/user-info`,
        contentType: 'application/json',
    })
        .done(function (res, status, xhr) {
            const username = res.username;
            const isAdmin = !!res.admin;

            if (!username) {
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

    // id 가 query 인 녀석 위에서 엔터를 누르면 execSearch() 함수를 실행하라는 뜻입니다.
    $('#query').on('keypress', function (e) {
        if (e.key == 'Enter') {
            execSearch();
        }
    });
    $('#close2').on('click', function () {
        $('#container2').removeClass('active');
    })
    $('#close3').on('click', function () {
        $('#container3').removeClass('active');
    })
    $('#close4').on('click', function () {
        $('#container4').removeClass('active');
    })
    $('.nav div.nav-see').on('click', function () {
        $('div.nav-see').addClass('active');
        $('div.nav-search').removeClass('active');

        $('#see-area').show();
        $('#search-area').hide();
    })
    $('.nav div.nav-search').on('click', function () {
        $('div.nav-see').removeClass('active');
        $('div.nav-search').addClass('active');

        $('#see-area').hide();
        $('#search-area').show();
    })

    $('#see-area').show();
    $('#search-area').hide();
})

// Folder 관련 기능
function openFolder(folderId) {
    folderTargetId = folderId;
    $("button.product-folder").removeClass("folder-active");
    if (!folderId) {
        $("button#folder-all").addClass('folder-active');
        $('#folder-delete-box').hide();
    } else {
        $(`button[value='${folderId}']`).addClass('folder-active');
        $('#folder-delete-box').show();
        document.getElementById("folder-delete-btn").value = folderId;
        document.getElementById("folder-delete-btn").textContent = "현재 폴더 삭제";
    }
    showschedule(folderId);
}

// 폴더 추가 팝업 열기
function openAddFolderPopup() {
    $('#container2').addClass('active');
}

// 폴더 Input 추가
function addFolderInput() {
    $('#folders-input').append(
        `<input type="text" class="folderToAdd" placeholder="추가할 폴더명">
       <span onclick="closeFolderInput(this)" style="margin-right:5px">
            <svg xmlns="http://www.w3.org/2000/svg" width="30px" fill="red" class="bi bi-x-circle-fill" viewBox="0 0 16 16">
              <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zM5.354 4.646a.5.5 0 1 0-.708.708L7.293 8l-2.647 2.646a.5.5 0 0 0 .708.708L8 8.707l2.646 2.647a.5.5 0 0 0 .708-.708L8.707 8l2.647-2.646a.5.5 0 0 0-.708-.708L8 7.293 5.354 4.646z"/>
            </svg>
       </span>
      `
    );
}

// 폴더 추가 팝업 닫기
function closeFolderInput(folder) {
    $(folder).prev().remove();
    $(folder).next().remove();
    $(folder).remove();
}

// 폴더 추가
function addFolder() {
    const folderNames = $('.folderToAdd').toArray().map(input => input.value);
    try {
        folderNames.forEach(name => {
            if (name === '') {
                alert('올바른 폴더명을 입력해주세요');
                throw new Error("stop loop");
            }
        });
    } catch (e) {
        console.log(e);
        return;
    }

    $.ajax({
        type: "POST",
        url: `/api/folders`,
        contentType: "application/json",
        data: JSON.stringify({folderNames})
    }).done(function (data, textStatus, xhr) {
        if(data !== '') {
            alert("중복된 폴더입니다.");
            return;
        }
        $('#container2').removeClass('active');
        alert('성공적으로 등록되었습니다.');
        window.location.reload();
    })
        .fail(function(xhr, textStatus, errorThrown) {
            alert("중복된 폴더입니다.");
        });
}

// 폴더에 일정 추가
function addInputForScheduleToFolder(scheduleId, button) {
    $.ajax({
        type: 'GET',
        url: `/api/folders`,
        success: function (folders) {
            const options = folders.map(folder => `<option value="${folder.id}">${folder.name}</option>`)
            const form = `
                <span>
                    <form id="folder-select" method="post" autocomplete="off" action="/api/schedules/${scheduleId}/folder">
                        <select name="folderId" form="folder-select">
                            ${options}
                        </select>
                        <input type="submit" value="추가" style="padding: 5px; font-size: 12px; margin-left: 5px;">
                    </form>
                </span>
            `;
            $(form).insertBefore(button);
            $(button).remove();
            $("#folder-select").on('submit', function (e) {
                e.preventDefault();
                $.ajax({
                    type: $(this).prop('method'),
                    url: $(this).prop('action'),
                    data: $(this).serialize(),
                }).done(function (data, textStatus, xhr) {
                    if(data !== '') {
                        alert("중복된 폴더입니다.");
                        return;
                    }
                    alert('성공적으로 등록되었습니다.');
                    window.location.reload();
                })
                    .fail(function(xhr, textStatus, errorThrown) {
                        alert("중복된 폴더입니다.");
                    });
            });
        },
        error(error, status, request) {
            logout();
        }
    });
}

// 폴더 삭제
function deleteFolder() {
    const id = $('#folder-delete-btn').val()

    $.ajax({
        type: "DELETE",
        url: `/api/folders/${id}`,
        success: function (response) {
            alert('폴더가 성공적으로 삭제되었습니다.');
            window.location.reload();
        },error: err => {
            // alert(err.responseJSON.message);
            alert("폴더 삭제 실패");
        }
    });
}

function logout() {
    // 토큰 삭제
    Cookies.remove('Authorization', {path: '/'});
    window.location.href = host + '/api/user/login-page';
}

function getToken() {
    let auth = Cookies.get('Authorization');

    if(auth === undefined) {
        return '';
    }

    return auth;
}