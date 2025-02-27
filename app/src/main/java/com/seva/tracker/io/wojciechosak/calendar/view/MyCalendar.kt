package com.seva.tracker.io.wojciechosak.calendar.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.seva.tracker.R
import com.seva.tracker.io.wojciechosak.calendar.view.CalendarConstants.INITIAL_PAGE_INDEX
import com.seva.tracker.io.wojciechosak.calendar.view.CalendarConstants.MAX_PAGES
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.daysUntil
import kotlinx.datetime.periodUntil
import kotlinx.datetime.plus
import java.util.Locale
import kotlin.math.floor

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyCalendar(
//    viewModel: CalendarHistoryViewModel,
//    value: List<GameEntity>,
    onDateSelected: (Int) -> Unit
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
        calendarView = { monthOffset ->
            CalendarView(
                day = { state ->
                    DayView(
                        isWhite = false,
                        date = state.date,
                        isCurrentMonth = state.date.month == dateToday.month && state.date.year == dateToday.year,
                        isSelected = state.date.toEpochDays() == selectedDate,
                        isDotVisible = true,
                        onClick = {
                            selectedDate = state.date.toEpochDays()
                            onDateSelected(state.date.toEpochDays().toInt())
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
                    MyWeekDays(it.name.first().toString().toUpperCase(Locale.ROOT))
                },
                header = { month, year ->
                    MyTittle(month, year,
                        onClickPrevious = {
                            if (!isScrolling) {
                                isScrolling = true
                                scope.launch {
//                                    var datee = getMinusOneDayOfMonth("$year, $month")
//                                    selectedMonth = datee.month
//                                    selectedYear = datee.year
//
//                                    viewModel.getAllMatchesOfCurrentMonth(datee.toString())
                                }
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
                                  //  var datee = getFirstDayOfMonth("$year, $month")
                                  //  selectedMonth = datee.month
                                  //  selectedYear = datee.year
                                 //   viewModel.getAllMatchesOfCurrentMonth(datee.toString())
                                }
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
                }
            )
        })
}

object CalendarConstants {
    // Compose 1.6.1 bug: https://issuetracker.google.com/issues/311414925, let's use fixed numbers for now.
    // internal const val MAX_PAGES = Int.MAX_VALUE
    internal const val MAX_PAGES = 100000
    internal const val INITIAL_PAGE_INDEX = MAX_PAGES / 2
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
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

@Composable
private fun DayView(
    isWhite: Boolean,
    date: LocalDate,
    isCurrentMonth: Boolean,
    onClick: () -> Unit = {},
    isSelected: Boolean,
    isDotVisible: Boolean = true,
    modifier: Modifier = Modifier,
    selectedMonth: Month,
    selectedYear: Int,
) {


    Box {
        val today = LocalDate.today()
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.aspectRatio(1f).padding(3.dp),
            contentPadding = PaddingValues(0.dp),
            border = BorderStroke(1.dp, if (isSelected) Color.White else Color.Blue),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = if (isSelected) Color.Cyan else Color.Magenta,
            ),
        ) {
            Text(
                text = "${date.dayOfMonth}",
                color = if (date == today && isCurrentMonth) Color.Red
                //   else if (!isCurrentMonth) TextSecondary
                else if (date.month == selectedMonth && date.year == selectedYear) Color.White
                else Color.White,
             //   style = if (date == today && isCurrentMonth) TextStyleLocal.semibold16 else TextStyleLocal.regular16,
                textAlign = TextAlign.Center
            )
        }
        if (isDotVisible) {
            Canvas(
                modifier =
                Modifier
                    .padding(bottom = 7.dp)
                    .size(4.dp)
                    .align(Alignment.BottomCenter),
                onDraw = { drawCircle(color = Color.White) },
            )
        }
    }
}


@Composable
private fun MyTittle(
    month: Month,
    year: Int,
    onClickPrevious: () -> Unit,
    onClickNext: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(bottom = 20.dp)
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.dp, Color.White, RoundedCornerShape(20.dp)
            ).background(Color.White),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_settings),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(start = 6.dp)
                .size(48.dp)
                .clickable { onClickPrevious() }
        )
        Text(
            text = month.name.capitalize() + ", " + year,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .weight(1f),
            color = Color.Red,
          //  style = TextStyleLocal.headerSmall,
            textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(id = R.drawable.ic_notes),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .padding(end = 6.dp)
                .size(48.dp)
                .clickable { onClickNext() }
        )
    }
}

@Composable
private fun MyWeekDays(
    day: String
) {
    Text(
        text = day.toString(),
        modifier = Modifier.padding(bottom = 10.dp).wrapContentSize(),
        color = Color.Red,
      //  style = TextStyleLocal.regular16,
    )
}


@OptIn(ExperimentalFoundationApi::class)
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

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun animateTo(
        target: LocalDate,
        pageOffsetFraction: Float = 0f,
        animationSpec: AnimationSpec<Float> = spring(stiffness = Spring.StiffnessMediumLow),
    ) {
        val initialPage = INITIAL_PAGE_INDEX
        val currentDate =
            when (mode) {
                AnimationMode.MONTH ->
                    startDate.plus(
                        (pagerState?.targetPage ?: 0) - initialPage,
                        DateTimeUnit.MONTH,
                    )

                AnimationMode.DAY ->
                    startDate.plus(
                        (pagerState?.targetPage ?: 0) - initialPage,
                        DateTimeUnit.DAY,
                    )

                AnimationMode.WEEK ->
                    startDate.plus(
                        (pagerState?.targetPage ?: 0) - initialPage,
                        DateTimeUnit.WEEK,
                    )
            }
        val targetDate =
            target.run {
                if (mode == AnimationMode.MONTH) {
                    copy(day = currentDate.dayOfMonth)
                } else {
                    this
                }
            }
        val diff = currentDate.periodUntil(targetDate)
        val offset =
            when (mode) {
                AnimationMode.MONTH -> diff.months + diff.years * 12
                AnimationMode.DAY -> currentDate.daysUntil(targetDate)
                AnimationMode.WEEK -> floor(currentDate.daysUntil(targetDate) / 7f).toInt()
            }
        if (initialPage + offset > 0) {
            pagerState?.animateScrollToPage(
                page = (pagerState?.settledPage ?: initialPage) + offset,
                pageOffsetFraction = pageOffsetFraction,
                animationSpec = animationSpec,
            )
        }
    }
}

@Composable
fun CalendarDa(
    state: DayState,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onClick: () -> Unit = {},
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) = with(state) {
    val today = LocalDate.today()
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
      //  shape = RoundedCornerShape(20.dp),
        border = BorderStroke(0.dp, if (isSelected) Color.Cyan else Color.Blue),
        contentPadding = PaddingValues(0.dp),
        interactionSource = interactionSource,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) Color.Cyan else Color.Blue,
        ),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                if (date == today) "Today ${date.dayOfMonth}" else date.dayOfMonth.toString(),
                color = if (isSelected) Color.Black else Color.White,
              //  style = if (date == today) TextStyleLocal.semibold14 else TextStyleLocal.semibold14,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun isDateInRange(date: LocalDate): Boolean {
    val today = LocalDate.today()
    val endDate = today.plus(7, DateTimeUnit.DAY)
    return date in today..endDate
}