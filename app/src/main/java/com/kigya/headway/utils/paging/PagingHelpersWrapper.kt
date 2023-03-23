package com.kigya.headway.utils.paging

import com.kigya.headway.data.model.NewsResponseDomainModel

class PagingHelpersWrapper(
    var page: Int,
    var response: NewsResponseDomainModel?,
)