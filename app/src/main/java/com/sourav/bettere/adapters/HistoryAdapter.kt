/*
 * Copyright 2020 Sourav Das
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sourav.bettere.adapters

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.sourav.bettere.R
import com.sourav.bettere.db.entity.ChargingHistory
import com.sourav.bettere.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(layoutID: Int, data: MutableList<ChargingHistory>) :
    BaseQuickAdapter<ChargingHistory, BaseViewHolder>(layoutID, data) {
    override fun convert(holder: BaseViewHolder, item: ChargingHistory) {
        holder.setText(R.id.historyDate, getDateTime(item.startTime))
        holder.setText(R.id.historyTo, item.startedFrom.toString().plus("%"))
        holder.setText(R.id.historyFrom, item.endedAt.toString().plus("%"))
        holder.setText(R.id.totalPercent, (item.endedAt - item.startedFrom).toString())
    }

    private fun getDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat(Constants.TIME_DATE_FORMAT)
        val now = Date(timestamp)
        return sdf.format(now)
    }
}