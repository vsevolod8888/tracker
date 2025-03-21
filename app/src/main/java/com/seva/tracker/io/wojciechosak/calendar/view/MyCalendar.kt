package com.seva.tracker.io.wojciechosak.calendar.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seva.tracker.data.room.RouteEntity
import com.seva.tracker.io.wojciechosak.calendar.view.CalendarConstants.INITIAL_PAGE_INDEX
import com.seva.tracker.io.wojciechosak.calendar.view.CalendarConstants.MAX_PAGES
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.periodUntil
import kotlinx.datetime.plus
import java.util.Locale
import kotlin.math.floor

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyCalendarBig(
    value: List<RouteEntity>,
    onDateSelected: (LocalDate) -> Unit
) {
    var dateToday = LocalDate.today()
    var scope = rememberCoroutineScope()
    var pagerState: PagerState = rememberPagerState(
        initialPage = 100000 / 2,
        pageCount = { 100000 },
    )
    val mutex = remember { Mutex() }
    var isScrolling by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(dateToday.toEpochDays()) }
    var selectedMonth by remember { mutableStateOf(dateToday.month) }
    var selectedYear by remember { mutableStateOf(dateToday.year) }
    HorizontalCalendarView(
        startDate = dateToday,
        pagerState = pagerState,
        modifier = Modifier.padding(horizontal = 16.dp),
        calendarView = { monthOffset ->
            CalendarView(
                header = { month, year ->
                    TittleCalendarBig(month, year,
                        onClickPrevious = {
                            if (!isScrolling) {
                                isScrolling = true
                                scope.launch {
                                    try {
                                        mutex.withLock {
                                            if (pagerState.currentPage > 0) {
                                                pagerState.scrollToPage(pagerState.currentPage - 1)
                                                delay(400)
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    } finally {
                                        isScrolling = false
                                    }
                                }
                            }
                        },
                        onClickNext = {
                            if (!isScrolling){
                                isScrolling = true
                                scope.launch {
                                    try {
                                        mutex.withLock {
                                            if (pagerState.currentPage < pagerState.pageCount - 1) {
                                                pagerState.scrollToPage(pagerState.currentPage + 1)
                                                delay(400)
                                            }
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    } finally {
                                        isScrolling = false
                                    }
                                }
                            }
                        })
                },
                day = { state ->
                    DayViewForBigCalendar(
                        date = state.date,
                        isCurrentMonth = state.date.month == dateToday.month && state.date.year == dateToday.year,
                        isSelected = state.date.toEpochDays() == selectedDate,
                        isDotVisible = value.any {
                            it.epochDays == state.date.toEpochDays().toInt()
                        },
                        onClick = {
                            selectedDate = state.date.toEpochDays()
                            onDateSelected(state.date)
                        },
                        selectedMonth = selectedMonth,
                        selectedYear = selectedYear,
                    )
                },
                config = rememberCalendarState(
                    startDate = dateToday,
                    monthOffset = monthOffset,
                ),
                dayOfWeekLabel = {
                    MyWeekDayForBigCalendar(it.name.first().toString().toUpperCase(Locale.ROOT))
                },

            )
        })
}

object CalendarConstants {
    internal const val MAX_PAGES = 100000
    internal const val INITIAL_PAGE_INDEX = MAX_PAGES / 2
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HorizontalCalendarView(
    startDate: LocalDate,
    pagerState: PagerState = rememberPagerState(
        initialPage = INITIAL_PAGE_INDEX,
        pageCount = { MAX_PAGES },
    ),
    modifier: Modifier = Modifier,
    pageSize: PageSize = PageSize.Fill,
    beyondBoundsPageCount: Int = 0,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    calendarAnimator: CalendarAnimator = CalendarAnimator(startDate),
    calendarView: @Composable (monthOffset: Int) -> Unit = {
        CalendarView(
            day = { dayState ->
                CalendarDay(
                    state = dayState,
                    onClick = { },
                )
            },
            config = rememberCalendarState(
                startDate = startDate,
                monthOffset = it,
            ),
        )
    },
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        pageSize = pageSize,
        beyondViewportPageCount = beyondBoundsPageCount,
        contentPadding = contentPadding,
    ) {
        val index = it - INITIAL_PAGE_INDEX
        calendarAnimator.updatePagerState(pagerState)
        LaunchedEffect(Unit) {
            calendarAnimator.setAnimationMode(CalendarAnimator.AnimationMode.MONTH)
        }
        Column { calendarView(index) }
    }
}

class CalendarAnimator(private val startDate: LocalDate) {
    enum class AnimationMode {
        MONTH,
        DAY,
        WEEK,
    }

    private var pagerState: PagerState? = null

    private var mode: AnimationMode = AnimationMode.MONTH

    internal fun updatePagerState(pagerState: PagerState) {
        this.pagerState = pagerState
    }

    internal fun setAnimationMode(mode: AnimationMode) {
        this.mode = mode
    }
}

fun isDateInRange(date: LocalDate): Boolean {
    val today = LocalDate.today()
    val endDate = today.plus(6, DateTimeUnit.DAY)
    return date in today..endDate
}