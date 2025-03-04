package com.seva.tracker.io.wojciechosak.calendar.view

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.pager.PagerState
import kotlinx.datetime.Month

import kotlinx.datetime.periodUntil
import kotlinx.datetime.plus
import kotlin.math.floor

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import com.seva.tracker.io.wojciechosak.calendar.view.CalendarConstants.INITIAL_PAGE_INDEX
import com.seva.tracker.io.wojciechosak.calendar.view.CalendarConstants.MAX_PAGES
import kotlinx.datetime.DayOfWeek


/**
 * Composable function to display a week view with selectable days.
 *
 * @param startDate The start date of the week view. Default is the current date.
 * @param minDate The minimum selectable date in the week view. Default is three months before the start date.
 * @param maxDate The maximum selectable date in the week view. Default is three months after the end date of the month containing the start date.
 * @param daysOffset The offset in days from the start date. Default is 0.
 * @param showDaysBesideRange Whether to show days beside the range. Default is true.
 * @param calendarAnimator The animator used for animating calendar transitions.
 * @param isActive A lambda function to determine if a date is considered active. Default is comparison with the current date.
 * @param modifier The modifier for styling and layout of the week view.
 * @param firstVisibleDate A callback invoked with the first visible date in the week view.
 * @param day The composable function to display each day item in the week view.
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeekView(
    startDate: LocalDate =
        Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .toLocalDate(),
    @SuppressLint("NewApi") minDate: LocalDate = startDate.copy(day = 1).minus(3, DateTimeUnit.MONTH),
    @SuppressLint("NewApi") maxDate: LocalDate =
        startDate.copy(day = monthLength(startDate.month, startDate.year))
            .plus(3, DateTimeUnit.MONTH),
    daysOffset: Int = 0,
    showDaysBesideRange: Boolean = true,
    calendarAnimator: CalendarAnimator = CalendarAnimator(startDate),
    isActive: (LocalDate) -> Boolean = {
        val today =
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toLocalDate()
        today == it
    },
    modifier: Modifier = Modifier,
    firstVisibleDate: (LocalDate) -> Unit = {},
    day: @Composable (dayState: DayState) -> Unit = { state ->
        weekDay(state) {
            CalendarDay(
                state,
                modifier = Modifier.width(58.dp),
            )
        }
    },
) {
    val minIndex = if (showDaysBesideRange) 0 else minDate.daysUntil(startDate)
    val maxIndex = if (showDaysBesideRange) MAX_PAGES else startDate.daysUntil(maxDate)
    val initialPageIndex = if (showDaysBesideRange) INITIAL_PAGE_INDEX else minIndex + daysOffset
    LaunchedEffect(Unit) {
        calendarAnimator.setAnimationMode(CalendarAnimator.AnimationMode.WEEK)
    }
    val pagerState =
        rememberPagerState(
            initialPage = initialPageIndex,
            pageCount = { minIndex + maxIndex },
        )
    HorizontalPager(
        state = pagerState,
        modifier = modifier,
    ) {
        val index = it - initialPageIndex // week number
        calendarAnimator.updatePagerState(pagerState)
        firstVisibleDate(startDate.plus(index * 7, DateTimeUnit.DAY))
        for (day in 0..6) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val newDate = startDate.plus(index * 7 + day, DateTimeUnit.DAY)
                day(
                    DayState(
                        date = newDate,
                        isActiveDay = isActive(newDate),
                        enabled = true,
                    ),
                )
            }
        }
    }
}



@Composable
private fun weekDay(
    state: DayState,
    function: @Composable () -> Unit,
) {
    Text(state.date.daySimpleName(), fontSize = 12.sp, textAlign = TextAlign.Center)
    function()
}


data class DayState(
    val date: LocalDate,
    val isActiveDay: Boolean = false,
    val isForPreviousMonth: Boolean = false,
    val isForNextMonth: Boolean = false,
    val enabled: Boolean = true,
)

fun isLeapYear(year: Int): Boolean = try {
    LocalDate(year, 2, 29)
    true
} catch (exception: IllegalArgumentException) {
    false
}

@RequiresApi(Build.VERSION_CODES.O)
fun monthLength(
    month: Month,
    year: Int,
): Int {
    val isLeapYear = isLeapYear(year)
    return when (month) {
        Month.FEBRUARY -> if (isLeapYear) 29 else 28
        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        else -> 31
    }
}

internal fun LocalDateTime.toLocalDate(): LocalDate = LocalDate(year, month, dayOfMonth)

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.copy(
    year: Int = this.year,
    month: Month = this.month,
    day: Int = this.dayOfMonth,
): LocalDate = try {
    LocalDate(year, month, day)
} catch (e: IllegalArgumentException) {
    LocalDate(year, month, monthLength(month, year))
}

fun LocalDate.Companion.today(): LocalDate = Clock.System.now()
    .toLocalDateTime(TimeZone.currentSystemDefault())
    .toLocalDate()

fun LocalDate.toMonthYear(): MonthYear = MonthYear(this.month, this.year)

fun LocalDate.daySimpleName() = dayOfWeek.name.substring(IntRange(0, 2))
data class MonthYear(val month: Month, val year: Int)

fun MonthYear.toLocalDate() = LocalDate(year, month, 1)

fun MonthYear.monthOffset(monthOffset: Int) = this
    .toLocalDate()
    .plus(monthOffset, DateTimeUnit.MONTH)
    .toMonthYear()




@Composable
fun CalendarDay(
    state: DayState,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) = with(state) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(1.dp, Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        interactionSource = interactionSource,
        enabled = enabled,
        colors =
        ButtonDefaults.outlinedButtonColors(
            contentColor =
            if (isForPreviousMonth || isForNextMonth) {
                Color.LightGray
            } else {
                if (isActiveDay)  Color.Green else Color.Blue
            },
        ),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${date.dayOfMonth}",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}


@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(
    config: MutableState<CalendarConfig>,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceEvenly,
    verticalArrangement: Arrangement.Vertical = Arrangement.SpaceEvenly,
    isActiveDay: (LocalDate) -> Boolean = { LocalDate.today() == it },
    day: @Composable (DayState) -> Unit = { dayState ->
        CalendarDay(dayState)
    },
    header: @Composable (month: Month, year: Int) -> Unit = { month, year ->
        MonthHeader(month, year)
    },
    dayOfWeekLabel: @Composable (dayOfWeek: DayOfWeek) -> Unit = { dayOfWeek ->
        Text(
            dayOfWeek.name.substring(IntRange(0, 2)),
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )
    },
    selectionMode: SelectionMode = SelectionMode.Single,
    onDateSelected: (List<LocalDate>) -> Unit = {},
    rangeConfig: RangeConfig? = null,
    modifier: Modifier = Modifier,
) {
    val yearMonth by remember { mutableStateOf(config.value.monthYear) }
    val daysInCurrentMonth by remember {
        mutableStateOf(
            monthLength(
                year = yearMonth.year,
                month = yearMonth.month,
            ),
        )
    }
    val previousMonthDays by remember { mutableStateOf(calculateVisibleDaysOfPreviousMonth(yearMonth)) }
    val nextMonthDays by remember {
        mutableStateOf(
            if (config.value.showNextMonthDays) {
                calculateVisibleDaysOfNextMonth(
                    yearMonth,
                )
            } else {
                0
            },
        )
    }
    Column {
        if (config.value.showHeader) {
            header(yearMonth.month, yearMonth.year)
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = horizontalArrangement,
            verticalArrangement = verticalArrangement,
            userScrollEnabled = false,
            modifier = modifier,
        ) {
            val state = config.value
            val weekDaysCount = if (state.showWeekdays) 7 else 0

            items(previousMonthDays + daysInCurrentMonth + nextMonthDays + weekDaysCount) { iteration ->
                Item(
                    iteration = iteration,
                    config = config,
                    weekDaysCount = weekDaysCount,
                    previousMonthDays = previousMonthDays,
                    daysInCurrentMonth = daysInCurrentMonth,
                    dayOfWeekLabel = dayOfWeekLabel,
                    yearMonth = yearMonth,
                    state = state,
                    selectionMode = selectionMode,
                    onDateSelected = onDateSelected,
                    isActiveDay = isActiveDay,
                    rangeConfig = rangeConfig,
                    day = day,
                )
            }
        }
    }
}

private fun calculateVisibleDaysOfPreviousMonth(monthYear: MonthYear): Int {
    val (month, year) = monthYear
    return LocalDate(year = year, month = month, dayOfMonth = 1).dayOfWeek.ordinal
}

@RequiresApi(Build.VERSION_CODES.O)
private fun calculateVisibleDaysOfNextMonth(monthYear: MonthYear): Int {
    val (month, year) = monthYear
    val daysInMonth = monthLength(month, year)
    val lastMonthDay = LocalDate(year = year, month = month, dayOfMonth = daysInMonth)
    return 6 - lastMonthDay.dayOfWeek.ordinal
}

@Stable
data class CalendarConfig(
    val minDate: LocalDate,
    val maxDate: LocalDate,
    val monthYear: MonthYear,
    val dayOfWeekOffset: Int,
    val showNextMonthDays: Boolean,
    val showPreviousMonthDays: Boolean,
    val showHeader: Boolean,
    val showWeekdays: Boolean,
    val selectedDates: List<LocalDate>,
)

data class RangeConfig(
    val color: Color = Color.Blue,
    val rangeIllustrator: RangeIllustrator = RoundedRangeIllustrator(color),
)

@Composable
fun rememberCalendarState(
    startDate: LocalDate,
    minDate: LocalDate = LocalDate(1971, 1, 1),
    maxDate: LocalDate = startDate.plus(15, DateTimeUnit.YEAR),
    monthOffset: Int,
    dayOfWeekOffset: Int = 0,
    showNextMonthDays: Boolean = true,
    showPreviousMonthDays: Boolean = true,
    showHeader: Boolean = true,
    showWeekdays: Boolean = true,
    selectedDates: MutableList<LocalDate> = mutableListOf(),
): MutableState<CalendarConfig> = remember {
    mutableStateOf(
        CalendarConfig(
            minDate = minDate,
            maxDate = maxDate,
            monthYear = startDate.plus(monthOffset, DateTimeUnit.MONTH).toMonthYear(),
            dayOfWeekOffset = dayOfWeekOffset,
            showNextMonthDays = showNextMonthDays,
            showPreviousMonthDays = showPreviousMonthDays,
            showHeader = showHeader,
            showWeekdays = showWeekdays,
            selectedDates = selectedDates,
        ),
    )
}

@Composable
fun MonthHeader(
    month: Month,
    year: Int,
) {
    val months =
        listOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December",
        )
    Text(
        "${months.getOrNull(month.ordinal)} $year",
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp, top = 0.dp),
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.ExtraLight,
        fontSize = 16.sp,
    )
}

sealed class SelectionMode {
    /**
     * Object representing single selection mode.
     */
    data object Single : SelectionMode()

    /**
     * Data class representing multiple selection mode with an optional buffer size.
     *
     * @property bufferSize The size of the buffer for multiple selection mode. Default is 2.
     */
    data class Multiply(val bufferSize: Int = 2) : SelectionMode()

    /**
     * Data class representing multiple selection mode with an optional buffer size.
     *
     * @property bufferSize The size of the buffer for multiple selection mode. Default is 2.
     */
    data object Range : SelectionMode()
}



interface RangeIllustrator {

    /**
     * Method to draw the end of a date range.
     *
     * @param drawScope The drawing scope used for rendering.
     */
    fun drawEnd(drawScope: DrawScope)

    /**
     * Method to draw the start of a date range.
     *
     * @param drawScope The drawing scope used for rendering.
     */
    fun drawStart(drawScope: DrawScope)

    /**
     * Method to draw the middle part of a date range.
     *
     * @param drawScope The drawing scope used for rendering.
     */
    fun drawMiddle(drawScope: DrawScope)
}

class RoundedRangeIllustrator(
    private val color: Color,
) : RangeIllustrator {

    /**
     * Method to draw the end of a date range with rounded corners.
     *
     * @param drawScope The drawing scope used for rendering.
     */
    override fun drawEnd(drawScope: DrawScope) {
        drawScope.apply {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 180f,
                useCenter = true,
            )
            drawRect(
                color = color,
                size = size.copy(width = size.width * 0.5f),
            )
        }
    }

    /**
     * Method to draw the start of a date range with rounded corners.
     *
     * @param drawScope The drawing scope used for rendering.
     */
    override fun drawStart(drawScope: DrawScope) {
        drawScope.apply {
            drawArc(
                color = color,
                startAngle = 180f,
                sweepAngle = 360f,
                useCenter = true,
            )
            drawRect(
                color = color,
                size = size.copy(width = size.width * 0.5f),
                topLeft = Offset(size.width * 0.5f, 0f),
            )
        }
    }

    /**
     * Method to draw the middle part of a date range with rounded corners.
     *
     * @param drawScope The drawing scope used for rendering.
     */
    override fun drawMiddle(drawScope: DrawScope) {
        drawScope.apply {
            drawRect(color = color)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
private fun Item(
    iteration: Int,
    config: MutableState<CalendarConfig>,
    weekDaysCount: Int,
    previousMonthDays: Int,
    daysInCurrentMonth: Int,
    dayOfWeekLabel: @Composable (DayOfWeek) -> Unit,
    yearMonth: MonthYear,
    state: CalendarConfig,
    selectionMode: SelectionMode,
    onDateSelected: (List<LocalDate>) -> Unit,
    isActiveDay: (LocalDate) -> Boolean,
    rangeConfig: RangeConfig?,
    day: @Composable (DayState) -> Unit,
) {
    val isWeekdayLabel = state.showWeekdays && iteration < weekDaysCount
    val previousMonthDay =
        iteration >= weekDaysCount && iteration < weekDaysCount + previousMonthDays
    val nextMonthDay =
        iteration >= weekDaysCount + previousMonthDays + daysInCurrentMonth
    var newDate = LocalDate(year = yearMonth.year, month = yearMonth.month, dayOfMonth = 1)

    if (previousMonthDay && config.value.showPreviousMonthDays) {
        newDate = newDate.plus(iteration - weekDaysCount - previousMonthDays, DateTimeUnit.DAY)
    } else if (nextMonthDay && config.value.showNextMonthDays) {
        newDate = newDate
            .plus(1, DateTimeUnit.MONTH)
            .plus(
                iteration - previousMonthDays - weekDaysCount - daysInCurrentMonth,
                DateTimeUnit.DAY,
            )
    } else if (!isWeekdayLabel) {
        newDate = newDate.plus(iteration - previousMonthDays - weekDaysCount, DateTimeUnit.DAY)
    }
    newDate = newDate.plus(state.dayOfWeekOffset, DateTimeUnit.DAY)

    if (state.showWeekdays && iteration + state.dayOfWeekOffset < 7 + state.dayOfWeekOffset) {
        val dayOfWeekIndex =
            if (iteration + state.dayOfWeekOffset >= DayOfWeek.entries.size) {
                iteration + state.dayOfWeekOffset - DayOfWeek.entries.size
            } else if (iteration + state.dayOfWeekOffset < 0) {
                DayOfWeek.entries.size + iteration + state.dayOfWeekOffset
            } else {
                iteration + state.dayOfWeekOffset
            }
        dayOfWeekLabel(DayOfWeek.entries[dayOfWeekIndex])
    } else if ((!state.showPreviousMonthDays && previousMonthDay) || (!state.showNextMonthDays && nextMonthDay)) {
        Text("")
    } else {
        val selectedDates = config.value.selectedDates
        Box(
            modifier = Modifier.passTouchGesture {
                val selectionList = selectDate(
                    date = newDate,
                    mode = selectionMode,
                    list = config.value.selectedDates,
                )
                config.value = config.value.copy(selectedDates = selectionList)
                onDateSelected(selectionList)
            }.drawRange(
                selectedDates = selectedDates,
                date = newDate,
                config = rangeConfig,
            ),
        ) {
            day(
                DayState(
                    date = newDate,
                    isActiveDay = isActiveDay(newDate),
                    isForPreviousMonth = previousMonthDay,
                    isForNextMonth = nextMonthDay,
                    enabled = newDate >= state.minDate && newDate <= state.maxDate,
                ),
            )
        }
    }
}

@Composable
fun Modifier.passTouchGesture(onTouchEvent: () -> Unit): Modifier = composed {
    pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown(requireUnconsumed = false)
            val change = waitForUpOrCancellation(pass = PointerEventPass.Initial)
            change?.let { onTouchEvent() }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
private fun selectDate(
    date: LocalDate,
    mode: SelectionMode,
    list: List<LocalDate>,
): List<LocalDate> {
    if (list.firstOrNull() == date) return list
    val result = mutableListOf<LocalDate>()
    result.addAll(list)
    when (mode) {
        is SelectionMode.Multiply -> {
            result.add(0, date)
            if (result.size > mode.bufferSize) {
                result.removeLast()
            }
        }

        SelectionMode.Range -> {
            result.add(0, date)
            if (result.size > 2) {
                result.clear()
                result.add(0, date)
            }
        }

        SelectionMode.Single -> {
            result.clear()
            result.add(date)
        }
    }
    return result
}

internal fun Modifier.drawRange(
    date: LocalDate,
    selectedDates: List<LocalDate>,
    config: RangeConfig? = null,
) = composed {
    if (config == null) return@composed this

    drawBehind {
        with(config) {
            val range =
                if (selectedDates.size == 2) {
                    if (selectedDates.first() >= selectedDates.last()) {
                        Pair(selectedDates.last(), selectedDates.first())
                    } else {
                        Pair(selectedDates.first(), selectedDates.last())
                    }
                } else {
                    null
                }

            if (range != null && date == range.second) {
                rangeIllustrator.drawEnd(this@drawBehind)
            } else if (range != null && date == range.first) {
                rangeIllustrator.drawStart(this@drawBehind)
            } else if (range != null && date in (range.first..range.second)) {
                rangeIllustrator.drawMiddle(this@drawBehind)
            }
        }
    }
}