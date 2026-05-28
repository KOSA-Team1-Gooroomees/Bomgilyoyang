let map = null;
let coords = {lat: 37.5665, lon: 126.9780}; // 기본 서울 시청
let currentMarkers = []; // 현재 지도에 표시된 시설 마커 배열
let parkMarkers = [];    // 현재 지도에 표시된 공원 마커 배열
let selectedFacilityId = null;

let globalFavorites = [];
let activeTab = 'search'; // 'search' 또는 'favorite'

// 페이징 및 검색 상태 관리
let keyword = '';
let lastId = null;
let lastValue = null;
let hasNextPage = true;
let isListLoading = false;

// DOM 원소 매핑
const facilityListEl = document.getElementById('facilityList');
const keywordInputEl = document.getElementById('keywordInput');
const listLoadingEl = document.getElementById('listLoading');
const detailSidebarEl = document.getElementById('detailSidebar');
const tabSearchBtn = document.getElementById('tabSearchBtn');
const tabFavoriteBtn = document.getElementById('tabFavoriteBtn');

// 문서 로드 완료 시 초기화
document.addEventListener("DOMContentLoaded", () => {
    // 최초 1회 즐겨찾기 로드
    fetchGlobalFavorites();

    if (typeof kakao !== 'undefined' && kakao.maps) {
        initGeolocation();
    } else {
        const checkKakaoInterval = setInterval(() => {
            if (typeof kakao !== 'undefined' && kakao.maps) {
                clearInterval(checkKakaoInterval);
                initGeolocation();
            }
        }, 100);
    }
});

// 서버에서 데이터를 다시 긁어와 리스트까지 갱신
async function fetchGlobalFavorites() {
    try {
        const response = await fetch('/api/favorites/me');
        if (response.ok) {
            globalFavorites = await response.json();

            // 현재 즐겨찾기 탭을 보고 있다면 화면 리스트를 서버 데이터로 덮어씌움
            if (activeTab === 'favorite') {
                renderFavoriteList();
            }
        }
    } catch (err) {
        console.error("즐겨찾기 갱신 실패:", err);
    }
}

// 특정 시설이 즐겨찾기 상태인지 판단하는 헬퍼
function isCurrentFavorite(facilityId) {
    return globalFavorites.some(fav => String(fav.facilityId) === String(facilityId));
}

// 1. 현재 사용자 위치 가져오기
function initGeolocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            (pos) => {
                coords.lat = pos.coords.latitude;
                coords.lon = pos.coords.longitude;
                initKakaoMap();
            },
            (err) => {
                console.error("위치 획득 실패, 기본 좌표로 맵을 초기화합니다.", err);
                initKakaoMap();
            }
        );
    } else {
        initKakaoMap();
    }
}

// 2. 카카오 지도 초기화 및 이벤트 리스너 바인딩
function initKakaoMap() {
    const container = document.getElementById('mapContainer');
    const options = {
        center: new kakao.maps.LatLng(coords.lat, coords.lon),
        level: 4
    };
    map = new kakao.maps.Map(container, options);

    new kakao.maps.Marker({
        position: new kakao.maps.LatLng(coords.lat, coords.lon),
        map: map
    });

    kakao.maps.event.addListener(map, 'idle', () => {
        const center = map.getCenter();
        const level = map.getLevel();
        let radius = 2;

        if (level <= 2) radius = 0.5;
        else if (level <= 4) radius = 2;
        else if (level <= 6) radius = 5;
        else if (level <= 8) radius = 15;
        else radius = 40;

        fetchFacilityMarkers(center.getLat(), center.getLng(), radius);
    });

    initEventListeners();
    fetchFacilities(true);
}

// 3. 지도 내부 영역 변경시 시설 마커 다운로드
let currentOverlay = null;

async function fetchFacilityMarkers(lat, lon, radius) {
    try {
        const response = await fetch(`/api/map/facilities/markers?lat=${lat}&lon=${lon}&radius=${radius}`);
        const markersData = await response.json();

        currentMarkers.forEach(m => m.setMap(null));
        currentMarkers = [];
        if (currentOverlay) currentOverlay.setMap(null);

        markersData.forEach(pos => {
            const markerPos = new kakao.maps.LatLng(pos.latitude, pos.longitude);
            const isSelected = selectedFacilityId === String(pos.id);

            const marker = new kakao.maps.Marker({
                position: markerPos,
                map: map,
                image: new kakao.maps.MarkerImage(
                    isSelected ? "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png" : "/images/icons/mark/basic_marker.png",
                    new kakao.maps.Size(24, 35)
                )
            });

            const overlayContent = document.createElement('div');
            overlayContent.className = `bg-white px-3 py-1 rounded-full shadow-lg border-2 text-xs font-bold whitespace-nowrap transition-all cursor-pointer ${
                isSelected ? "border-blue-600 text-blue-700 scale-110" : "border-gray-400 text-gray-600 hover:border-blue-400"
            }`;
            overlayContent.innerText = pos.facilityName;

            overlayContent.onclick = (e) => {
                e.stopPropagation();
                handleOpenDetail(pos.id, markerPos);
            };

            const customOverlay = new kakao.maps.CustomOverlay({
                position: markerPos,
                content: overlayContent,
                yAnchor: 2.3
            });

            if (isSelected) {
                customOverlay.setMap(map);
                currentOverlay = customOverlay;
            } else {
                kakao.maps.event.addListener(marker, 'mouseover', () => {
                    customOverlay.setMap(map);
                });
                kakao.maps.event.addListener(marker, 'mouseout', () => {
                    if (selectedFacilityId !== String(pos.id)) customOverlay.setMap(null);
                });
            }

            kakao.maps.event.addListener(marker, 'click', () => {
                if (currentOverlay) currentOverlay.setMap(null);
                customOverlay.setMap(map);
                currentOverlay = customOverlay;
                handleOpenDetail(pos.id, markerPos);
            });

            currentMarkers.push(marker);
        });
    } catch (err) {
        console.error("마커 동기화 오류:", err);
    }
}

// 4. 왼쪽 검색결과 리스트 출력
async function fetchFacilities(isFirstLoad = false) {
    if (activeTab !== 'search') return;
    if (isListLoading || (!isFirstLoad && !hasNextPage)) return;

    isListLoading = true;
    listLoadingEl.classList.remove('hidden');

    if (isFirstLoad) {
        lastId = null;
        lastValue = null;
        facilityListEl.innerHTML = '';
    }

    try {
        let url = `/api/map/facilities?keyword=${encodeURIComponent(keyword)}&size=10&sort=distance&userLat=${coords.lat}&userLon=${coords.lon}`;
        if (lastId && lastValue) {
            url += `&lastId=${lastId}&lastValue=${lastValue}`;
        }

        const response = await fetch(url);
        const data = await response.json();

        if (data.length === 0 && isFirstLoad) {
            facilityListEl.innerHTML = `<div class="text-center py-8 text-gray-400 text-sm">검색 결과가 없습니다.</div>`;
            hasNextPage = false;
        } else {
            data.forEach((item, index) => {
                const itemHtml = createFacilityItemHtml(item);
                facilityListEl.insertAdjacentHTML('beforeend', itemHtml);

                if (data.length === 10 && index === data.length - 1) {
                    lastId = item.id;
                    lastValue = item.distance;
                }
            });
            hasNextPage = data.length === 10;
        }
    } catch (err) {
        console.error("리스트 로드 오류:", err);
    } finally {
        isListLoading = false;
        listLoadingEl.classList.add('hidden');
    }
}

// 4-2. 즐겨찾기 리스트 렌더링
function renderFavoriteList() {
    facilityListEl.innerHTML = '';

    if (globalFavorites.length === 0) {
        facilityListEl.innerHTML = `<div class="text-center py-8 text-gray-400 text-sm">등록된 즐겨찾기가 없습니다.</div>`;
        return;
    }

    globalFavorites.forEach(fav => {
        const targetFacility = fav.facility ? fav.facility : fav;
        const itemHtml = createFacilityItemHtml(targetFacility);
        facilityListEl.insertAdjacentHTML('beforeend', itemHtml);
    });
}

// 5. 컴포넌트 HTML 생성
function createFacilityItemHtml(item) {
    const score = item.facilityScore || 0;
    const icons =
        Array(score).fill('<img src="/images/icons/score-true.png" alt="추천별" class="w-7 h-7 object-contain border border-transparent rounded-full">').join('') +
        Array(5 - score).fill('<img src="/images/icons/score-false.png" alt="빈별" class="w-7 h-7 object-contain rounded-full">').join('');

    const isSelected = selectedFacilityId === String(item.id);
    const bgClass = isSelected ? 'bg-bg-soft border-green-400' : 'bg-white border-gray-100 hover:border-blue-200';

    return `
    <div onclick="handleOpenDetail('${item.id}', new kakao.maps.LatLng(${item.latitude}, ${item.longitude}))" 
         data-id="${item.id}"
         class="facility-item p-4 rounded-2xl border ${bgClass} shadow-sm transition-all cursor-pointer group flex gap-4">
        <div class="w-20 h-20 shrink-0 overflow-hidden rounded-xl bg-gray-100 border border-gray-50">
            <img src="${item.facilityImage}" alt="${item.facilityName}" class="w-full h-full object-cover transition-transform group-hover:scale-110" />
        </div>
        <div class="flex-1 min-w-0">
            <div class="flex justify-between items-start gap-2">
                <h3 class="font-bold text-m truncate text-gray-800">${item.facilityName}</h3>
                <span class="text-[10px] font-bold text-blue-500 bg-blue-50 px-1.5 py-0.5 rounded-md shrink-0">${item.distance ? item.distance.toFixed(1) : 0}km</span>
            </div>
            <div class="flex items-center gap-0.5 mt-0.5 ">${icons}</div>
            <div class="mt-2 space-y-1">
                <p class="text-[11px] text-gray-500 flex items-start"><span class="mr-1 text-blue-400">📍</span><span class="truncate">${item.newAddress}</span></p>
                ${item.facilityTel ? `<p class="text-[11px] text-gray-400 flex items-center"><span class="mr-1 text-green-500">📞</span><span class="truncate">${item.facilityTel}</span></p>` : ''}
            </div>
        </div>
    </div>`;
}

// 6. 시설 상세 정보 조회 및 사이드바 바인딩
async function handleOpenDetail(facilityId, latLng) {
    selectedFacilityId = String(facilityId);
    if (map && latLng) map.panTo(latLng);

    document.querySelectorAll('.facility-item').forEach(el => {
        if (el.getAttribute('data-id') === selectedFacilityId) {
            el.classList.remove('bg-white', 'border-gray-100', 'hover:border-blue-200');
            el.classList.add('bg-bg-soft', 'border-green-400');
        } else {
            el.classList.remove('bg-bg-soft', 'border-green-400');
            el.classList.add('bg-white', 'border-gray-100', 'hover:border-blue-200');
        }
    });

    try {
        const response = await fetch(`/api/map/facilities/${facilityId}`);
        if (!response.ok) return;
        const detail = await response.json();

        document.getElementById("detailImage").src = detail.facilityImage;
        document.getElementById('detailName').innerText = detail.facilityName;
        document.getElementById('detailScore').innerText = Number(detail.facilityScore || 0).toFixed(1);
        document.getElementById('capacityCnt').innerText = detail.capacityCnt || 0;
        document.getElementById('currentCnt').innerText = detail.currentCnt || 0;
        document.getElementById('detailNewAddress').innerText = detail.newAddress;
        document.getElementById('detailLotAddress').innerText = detail.lotAddress || '지번 주소 없음';

        const telEl = document.getElementById('detailTel');
        if (detail.facilityTel) {
            telEl.innerText = detail.facilityTel;
            document.getElementById('detailPhoneContainer').classList.remove('hidden');
        } else {
            document.getElementById('detailPhoneContainer').classList.add('hidden');
        }

        // 하트 상태 스타일 토글
        toggleFavoriteBtnStyle(isCurrentFavorite(facilityId));
        fetchNearbyParks(facilityId);

        detailSidebarEl.classList.remove('hidden');
    } catch (err) {
        console.error("상세조회 에러:", err);
    }
}

// 7. 인근 공원 조회 및 지도 마커 맵핑
async function fetchNearbyParks(facilityId) {
    try {
        const response = await fetch(`/api/map/facilities/markers/${facilityId}/nearby-parks?radius=3`);
        const parks = await response.json();

        document.getElementById('parkCount').innerText = parks.length;
        const parkContainer = document.getElementById('nearbyParkList');
        parkContainer.innerHTML = '';

        parkMarkers.forEach(m => m.setMap(null));
        parkMarkers = [];

        if (parks.length === 0) {
            parkContainer.innerHTML = `
                <div class="py-8 text-center bg-gray-50 rounded-xl border border-dashed border-gray-200">
                    <p class="text-sm text-gray-400 italic font-medium">주변 공원 정보가 없습니다.</p>
                </div>`;
            return;
        }

        parks.forEach(park => {
            const parkHtml = `
                <div class="p-4 rounded-xl border border-gray-100 bg-white shadow-sm flex flex-col gap-3">
                    <div class="flex flex-col gap-1.5">
                        <span class="text-[10px] font-bold text-green-600 bg-green-50 px-2 py-0.5 rounded w-fit border border-green-100">${park.category}</span>
                        <h4 class="text-[15px] font-bold text-gray-800 leading-tight flex items-center gap-1.5">
                            <img src="/images/icons/score-true.png" alt="공원" class="w-5 h-5 object-contain border border-transparent rounded-full"> 
                            <span>${park.name}</span>
                        </h4>
                    </div>
                    <div class="space-y-2 border-t border-gray-50 pt-3 text-[13px] text-gray-600">
                        <p>📍 ${park.lotAddress}</p>
                        <p class="text-[12px] text-gray-500">📐 면적: <span class="font-medium text-gray-700">${park.area ? park.area.toLocaleString() : '정보없음'} m²</span></p>
                    </div>
                </div>`;
            parkContainer.insertAdjacentHTML('beforeend', parkHtml);

            const parkPos = new kakao.maps.LatLng(park.latitude, park.longitude);
            const marker = new kakao.maps.Marker({
                position: parkPos,
                map: map,
                image: new kakao.maps.MarkerImage("/images/icons/mark/park_marker.png", new kakao.maps.Size(24, 35))
            });

            const parkOverlayContent = document.createElement('div');
            parkOverlayContent.className = "bg-white p-3 rounded-xl shadow-2xl border-2 border-green-500 min-w-[200px] relative mb-2";
            parkOverlayContent.innerHTML = `
                <div class="flex flex-col gap-1 mb-2">
                    <span class="text-[10px] font-bold text-green-600 bg-green-50 px-1.5 py-0.5 rounded w-fit">${park.category || '공원'}</span>
                    <h4 class="text-sm font-extrabold text-gray-800 flex items-center gap-1">🌳 ${park.name}</h4>
                </div>
                <div class="space-y-1 border-t border-gray-100 pt-2 text-[11px] text-gray-600">
                    <p class="break-all">📍 ${park.lotAddress}</p>
                    ${park.area ? `<p>📐 면적: ${park.area.toLocaleString()} m²</p>` : ''}
                </div>
                <div class="absolute bottom-[-8px] left-1/2 -translate-x-1/2 w-4 h-4 bg-white border-r-2 border-b-2 border-green-500 rotate-45"></div>
            `;

            const parkOverlay = new kakao.maps.CustomOverlay({
                position: parkPos,
                content: parkOverlayContent,
                yAnchor: 1.4
            });
            kakao.maps.event.addListener(marker, 'mouseover', () => {
                parkOverlay.setMap(map);
            });
            kakao.maps.event.addListener(marker, 'mouseout', () => {
                parkOverlay.setMap(null);
            });

            parkMarkers.push(marker);
        });
    } catch (err) {
        console.error("공원 조회 실패:", err);
    }
}

function toggleFavoriteBtnStyle(isFav) {
    const btn = document.getElementById('favoriteBtn');
    if (isFav) {
        btn.className = "shrink-0 w-10 h-10 flex items-center justify-center rounded-full border transition-all bg-red-50 border-red-100 text-red-500";
        btn.querySelector('svg').setAttribute('fill', 'currentColor');
    } else {
        btn.className = "shrink-0 w-10 h-10 flex items-center justify-center rounded-full border transition-all bg-gray-50 border-gray-100 text-gray-400 hover:text-red-400";
        btn.querySelector('svg').setAttribute('fill', 'none');
    }
}

// 9. 이벤트 리스너 바인딩
function initEventListeners() {

    // 탭 클릭 핸들러
    tabSearchBtn.addEventListener('click', () => {
        activeTab = 'search';
        tabSearchBtn.className = "flex-1 py-2 text-center text-sm font-semibold border-b-2 border-blue-500 text-blue-600 transition-all outline-none";
        tabFavoriteBtn.className = "flex-1 py-2 text-center text-sm font-semibold border-b-2 border-transparent text-gray-400 hover:text-gray-600 transition-all outline-none";
        fetchFacilities(true);
    });

    tabFavoriteBtn.addEventListener('click', () => {
        activeTab = 'favorite';
        tabSearchBtn.className = "flex-1 py-2 text-center text-sm font-semibold border-b-2 border-transparent text-gray-400 hover:text-gray-600 transition-all outline-none";
        tabFavoriteBtn.className = "flex-1 py-2 text-center text-sm font-semibold border-b-2 border-blue-500 text-blue-600 transition-all outline-none";
        renderFavoriteList();
    });

    // 검색 입력창 디바운스
    let searchTimer;
    keywordInputEl.addEventListener('input', (e) => {
        clearTimeout(searchTimer);
        keyword = e.target.value;
        searchTimer = setTimeout(() => {
            if (activeTab === 'favorite') {
                tabSearchBtn.click();
            } else {
                fetchFacilities(true);
            }
        }, 400);
    });

    // 무한 스크롤
    facilityListEl.addEventListener('scroll', () => {
        if (activeTab !== 'search') return;
        if (facilityListEl.scrollTop + facilityListEl.clientHeight >= facilityListEl.scrollHeight - 20) {
            fetchFacilities(false);
        }
    });

    // 상세창 닫기
    document.getElementById('closeSidebarBtn').addEventListener('click', () => {
        detailSidebarEl.classList.add('hidden');
        selectedFacilityId = null;
        parkMarkers.forEach(m => m.setMap(null));

        document.querySelectorAll('.facility-item').forEach(el => {
            el.classList.remove('bg-bg-soft', 'border-green-400');
            el.classList.add('bg-white', 'border-gray-100', 'hover:border-blue-200');
        });
    });

    // 즐겨찾기 클릭 제어
    document.getElementById('favoriteBtn').addEventListener('click', async () => {
        const alreadyFav = isCurrentFavorite(selectedFacilityId);
        const method = alreadyFav ? 'DELETE' : 'POST';
        const url = alreadyFav ? '/api/favorites/me' : '/api/favorites';
        const body = JSON.stringify({facilityId: selectedFacilityId});

        try {
            const response = await fetch(url, {
                method: method,
                headers: {'Content-Type': 'application/json'},
                body: body
            });

            if (response.ok) {
                await fetchGlobalFavorites();
                toggleFavoriteBtnStyle(isCurrentFavorite(selectedFacilityId));
            } else if (response.status === 401) {
                alert("즐겨찾기 기능은 로그인 후 이용 가능합니다.");
                window.location.href = '/login';
            } else {
                alert("즐겨찾기 처리 중 오류가 발생했습니다.");
            }
        } catch (err) {
            console.error(err);
            alert("즐겨찾기 처리 중 에러 발생");
        }
    });
}