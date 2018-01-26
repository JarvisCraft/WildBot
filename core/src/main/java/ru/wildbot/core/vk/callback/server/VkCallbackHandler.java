/*
 * Copyright 2017 Peter P. (JARvis PROgrammer)
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

package ru.wildbot.core.vk.callback.server;

import com.vk.api.sdk.callback.CallbackApi;
import com.vk.api.sdk.callback.objects.board.CallbackBoardPostDelete;
import com.vk.api.sdk.callback.objects.group.*;
import com.vk.api.sdk.callback.objects.market.CallbackMarketComment;
import com.vk.api.sdk.callback.objects.market.CallbackMarketCommentDelete;
import com.vk.api.sdk.callback.objects.messages.CallbackMessageAllow;
import com.vk.api.sdk.callback.objects.messages.CallbackMessageDeny;
import com.vk.api.sdk.callback.objects.photo.CallbackPhotoComment;
import com.vk.api.sdk.callback.objects.photo.CallbackPhotoCommentDelete;
import com.vk.api.sdk.callback.objects.poll.CallbackPollVoteNew;
import com.vk.api.sdk.callback.objects.user.CallbackUserBlock;
import com.vk.api.sdk.callback.objects.user.CallbackUserUnblock;
import com.vk.api.sdk.callback.objects.video.CallbackVideoComment;
import com.vk.api.sdk.callback.objects.video.CallbackVideoCommentDelete;
import com.vk.api.sdk.callback.objects.wall.CallbackWallComment;
import com.vk.api.sdk.callback.objects.wall.CallbackWallCommentDelete;
import com.vk.api.sdk.objects.audio.Audio;
import com.vk.api.sdk.objects.board.TopicComment;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.video.Video;
import com.vk.api.sdk.objects.wall.WallPost;
import ru.wildbot.core.WildBotCore;
import ru.wildbot.core.api.event.EventManager;
import ru.wildbot.core.vk.callback.event.*;

public class VkCallbackHandler extends CallbackApi {
    private EventManager eventManager;

    public VkCallbackHandler() {
        this.eventManager = WildBotCore.getInstance().getEventManager();
    }

    @Override
    public void messageNew(final Integer groupId, final Message message) {
        eventManager.callEvents(new VkMessageNewEvent(groupId, message));
    }

    @Override
    public void messageReply(final Integer groupId, final Message message) {
        eventManager.callEvents(new VkMessageReplyEvent(groupId, message));
    }

    @Override
    public void messageAllow(final Integer groupId, final CallbackMessageAllow message) {
        eventManager.callEvents(new VkMessageAllowEvent(groupId, message));
    }

    @Override
    public void messageDeny(final Integer groupId, final CallbackMessageDeny message) {
        eventManager.callEvents(new VkMessageDenyEvent(groupId, message));
    }

    @Override
    public void photoNew(final Integer groupId, final Photo message) {
        eventManager.callEvents(new VkPhotoNewEvent(groupId, message));
    }

    @Override
    public void photoCommentNew(final Integer groupId, final CallbackPhotoComment message) {
        eventManager.callEvents(new VkPhotoCommandNewEvent(groupId, message));
    }

    @Override
    public void photoCommentEdit(final Integer groupId, final CallbackPhotoComment message) {
        eventManager.callEvents(new VkPhotoCommentEditEvent(groupId, message));
    }

    @Override
    public void photoCommentRestore(final Integer groupId, final CallbackPhotoComment message) {
        eventManager.callEvents(new VkPhotoCommentRestoreEvent(groupId, message));
    }

    @Override
    public void photoCommentDelete(final Integer groupId, final CallbackPhotoCommentDelete message) {
        eventManager.callEvents(new VkPhotoCommentDeleteEvent(groupId, message));
    }

    @Override
    public void audioNew(final Integer groupId, final Audio message) {
        eventManager.callEvents(new VkAudioNewEvent(groupId, message));
    }

    @Override
    public void videoNew(final Integer groupId, final Video message) {
        eventManager.callEvents(new VkVideoNewEvent(groupId, message));
    }

    @Override
    public void videoCommentNew(final Integer groupId, final CallbackVideoComment message) {
        eventManager.callEvents(new VkVideoCommentNewEvent(groupId, message));
    }

    @Override
    public void videoCommentEdit(final Integer groupId, final CallbackVideoComment message) {
        eventManager.callEvents(new VkVideoCommentEditEvent(groupId, message));
    }

    @Override
    public void videoCommentRestore(final Integer groupId, final CallbackVideoComment message) {
        eventManager.callEvents(new VkVideoCommentRestoreEvent(groupId, message));
    }

    @Override
    public void videoCommentDelete(final Integer groupId, final CallbackVideoCommentDelete message) {
        eventManager.callEvents(new VkVideoCommentDeleteEvent(groupId, message));
    }

    @Override
    public void wallPostNew(final Integer groupId, final WallPost message) {
        eventManager.callEvents(new VkWallPostNewEvent(groupId, message));
    }

    @Override
    public void wallRepost(final Integer groupId, final WallPost message) {
        eventManager.callEvents(new VkWallRepostEvent(groupId, message));
    }

    @Override
    public void wallReplyNew(final Integer groupId, final CallbackWallComment object) {
        eventManager.callEvents(new VkWallReplyNewEvent(groupId, object));
    }

    @Override
    public void wallReplyEdit(final Integer groupId, final CallbackWallComment message) {
        eventManager.callEvents(new VkWallReplyEditEvent(groupId, message));
    }

    @Override
    public void wallReplyRestore(final Integer groupId, final CallbackWallComment message) {
        eventManager.callEvents(new VkWallReplyRestoreEvent(groupId, message));
    }

    @Override
    public void wallReplyDelete(final Integer groupId, final CallbackWallCommentDelete message) {
        eventManager.callEvents(new VkWallReplyDeleteEvent(groupId, message));
    }

    @Override
    public void boardPostNew(final Integer groupId, final TopicComment message) {
        eventManager.callEvents(new VkBoardPostNewEvent(groupId, message));
    }

    @Override
    public void boardPostEdit(final Integer groupId, final TopicComment message) {
        eventManager.callEvents(new VkBoardPostEditEvent(groupId, message));
    }

    @Override
    public void boardPostRestore(final Integer groupId, final TopicComment message) {
        eventManager.callEvents(new VkBoardPostRestoreEvent(groupId, message));
    }

    @Override
    public void boardPostDelete(final Integer groupId, final CallbackBoardPostDelete message) {
        eventManager.callEvents(new VkBoardPostDeleteEvent(groupId, message));
    }

    @Override
    public void marketCommentNew(final Integer groupId, final CallbackMarketComment message) {
        eventManager.callEvents(new VkMarketCommentNewEvent(groupId, message));
    }

    @Override
    public void marketCommentEdit(final Integer groupId, final CallbackMarketComment message) {
        eventManager.callEvents(new VkMarketCommentEditEvent(groupId, message));
    }

    @Override
    public void marketCommentRestore(final Integer groupId, final CallbackMarketComment message) {
        eventManager.callEvents(new VkMarketCommentRestoreEvent(groupId, message));
    }

    @Override
    public void marketCommentDelete(final Integer groupId, final CallbackMarketCommentDelete message) {
        eventManager.callEvents(new VkMarketCommentDeleteEvent(groupId, message));
    }

    @Override
    public void groupLeave(final Integer groupId, final CallbackGroupLeave message) {
        eventManager.callEvents(new VkGroupLeaveEvent(groupId, message));
    }

    @Override
    public void groupJoin(final Integer groupId, final CallbackGroupJoin message) {
        eventManager.callEvents(new VkGroupJoinEvent(groupId, message));
    }

    @Override
    public void groupChangeSettings(final Integer groupId, final CallbackGroupChangeSettings message) {
        eventManager.callEvents(new VkGroupChangeSettingsEvent(groupId, message));
    }

    @Override
    public void groupChangePhoto(final Integer groupId, final CallbackGroupChangePhoto message) {
        eventManager.callEvents(new VkGroupChangePhotoEvent(groupId, message));
    }

    @Override
    public void groupOfficersEdit(final Integer groupId, final CallbackGroupOfficersEdit message) {
        eventManager.callEvents(new VkGroupOfficersEditEvent(groupId, message));
    }

    @Override
    public void pollVoteNew(final Integer groupId, final CallbackPollVoteNew message) {
        eventManager.callEvents(new VkPollVoteNewEvent(groupId, message));
    }

    @Override
    public void userBlock(final Integer groupId, final CallbackUserBlock message) {
        eventManager.callEvents(new VkUserBlockEvent(groupId, message));
    }

    @Override
    public void userUnblock(final Integer groupId, final CallbackUserUnblock message) {
        eventManager.callEvents(new VkUserUnblockEvent(groupId, message));
    }
}
