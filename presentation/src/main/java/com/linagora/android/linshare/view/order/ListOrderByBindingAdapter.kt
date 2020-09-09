/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

package com.linagora.android.linshare.view.order

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.linagora.android.linshare.R
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.model.order.OrderTypeName

@BindingAdapter("orderByListConfigurationName")
fun bindingOrderByListConfigurationName(
    textView: TextView,
    orderListConfigurationType: OrderListConfigurationType
) {
    val configurationNameId = when (orderListConfigurationType) {
        OrderListConfigurationType.AscendingModificationDate, OrderListConfigurationType.DescendingModificationDate ->
            R.string.order_by_modification_date
        OrderListConfigurationType.AscendingCreationDate, OrderListConfigurationType.DescendingCreationDate ->
            R.string.order_by_creation_date
        OrderListConfigurationType.AscendingName, OrderListConfigurationType.DescendingName ->
            R.string.order_by_name
        OrderListConfigurationType.AscendingFileSize, OrderListConfigurationType.DescendingFileSize ->
            R.string.order_by_file_size
        OrderListConfigurationType.AscendingShared, OrderListConfigurationType.DescendingShared ->
            R.string.order_by_shared
    }
    textView.text = textView.context.getString(configurationNameId)
}

@BindingAdapter("orderByListTypeImage")
fun bindingOrderByListType(
    imageView: ImageView,
    orderListConfigurationType: OrderListConfigurationType
) {
    val imageResource = orderListConfigurationType.takeIf { it.isAscending() }
        ?.let { R.drawable.ic_arrow_up_with_line }
        ?: R.drawable.ic_arrow_down_with_line
    imageView.setImageResource(imageResource)
}

@BindingAdapter("selectedOrderTypeNameTextColor", "currentOrderTypeNameTextColor", requireAll = true)
fun bindingOrderTypeNameTextColor(
    textView: TextView,
    selectedOrderTypeNameTextColor: OrderTypeName,
    currentOrderTypeNameTextColor: OrderTypeName
) {
    val textColorId = selectedOrderTypeNameTextColor.takeIf { it == currentOrderTypeNameTextColor }
        ?.let { R.color.colorPrimary }
        ?: R.color.greyPrimary
    textView.setTextColor(ContextCompat.getColor(textView.context, textColorId))
}
