package com.kotlinconf.workshop.issues.ui

import com.kotlinconf.workshop.AddCommentToIssueEvent
import com.kotlinconf.workshop.Comment
import com.kotlinconf.workshop.CreateIssueEvent
import com.kotlinconf.workshop.Issue
import com.kotlinconf.workshop.issues.network.IssuesService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IssuesViewModel(val issuesService: IssuesService) {
    private val scope = CoroutineScope(SupervisorJob())

    val issueFlow = flowOf<List<Issue>>()

    val commentFlow = flowOf<List<Comment>>()


}