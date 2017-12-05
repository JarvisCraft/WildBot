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
    public void messageNew(Integer groupId, Message message) {
        eventManager.callEvents(new VkMessageNewEvent(groupId, message));
    }

    @Override
    public void messageReply(Integer groupId, Message message) {
        eventManager.callEvents(new VkMessageReplyEvent(groupId, message));
    }

    @Override
    public void messageAllow(Integer groupId, CallbackMessageAllow message) {
        eventManager.callEvents(new VkMessageAllowEvent(groupId, message));
    }

    @Override
    public void messageDeny(Integer groupId, CallbackMessageDeny message) {
        eventManager.callEvents(new VkMessageDenyEvent(groupId, message));
    }

    @Override
    public void photoNew(Integer groupId, Photo message) {
        eventManager.callEvents(new VkPhotoNewEvent(groupId, message));
    }

    @Override
    public void photoCommentNew(Integer groupId, CallbackPhotoComment message) {
        eventManager.callEvents(new VkPhotoCommandNewEvent(groupId, message));
    }

    @Override
    public void photoCommentEdit(Integer groupId, CallbackPhotoComment message) {
        eventManager.callEvents(new VkPhotoCommentEditEvent(groupId, message));
    }

    @Override
    public void photoCommentRestore(Integer groupId, CallbackPhotoComment message) {
        eventManager.callEvents(new VkPhotoCommentRestoreEvent(groupId, message));
    }

    @Override
    public void photoCommentDelete(Integer groupId, CallbackPhotoCommentDelete message) {
        eventManager.callEvents(new VkPhotoCommentDeleteEvent(groupId, message));
    }

    @Override
    public void audioNew(Integer groupId, Audio message) {
        eventManager.callEvents(new VkAudioNewEvent(groupId, message));
    }

    @Override
    public void videoNew(Integer groupId, Video message) {
        eventManager.callEvents(new VkVideoNewEvent(groupId, message));
    }

    @Override
    public void videoCommentNew(Integer groupId, CallbackVideoComment message) {
        eventManager.callEvents(new VkVideoCommentNewEvent(groupId, message));
    }

    @Override
    public void videoCommentEdit(Integer groupId, CallbackVideoComment message) {
        eventManager.callEvents(new VkVideoCommentEditEvent(groupId, message));
    }

    @Override
    public void videoCommentRestore(Integer groupId, CallbackVideoComment message) {
        eventManager.callEvents(new VkVideoCommentRestoreEvent(groupId, message));
    }

    @Override
    public void videoCommentDelete(Integer groupId, CallbackVideoCommentDelete message) {
        eventManager.callEvents(new VkVideoCommentDeleteEvent(groupId, message));
    }

    @Override
    public void wallPostNew(Integer groupId, WallPost message) {
        eventManager.callEvents(new VkWallPostNewEvent(groupId, message));
    }

    @Override
    public void wallRepost(Integer groupId, WallPost message) {
        eventManager.callEvents(new VkWallRepostEvent(groupId, message));
    }

    @Override
    public void wallReplyNew(Integer groupId, CallbackWallComment object) {
        eventManager.callEvents(new VkWallReplyNewEvent(groupId, object));
    }

    @Override
    public void wallReplyEdit(Integer groupId, CallbackWallComment message) {
        eventManager.callEvents(new VkWallReplyEditEvent(groupId, message));
    }

    @Override
    public void wallReplyRestore(Integer groupId, CallbackWallComment message) {
        eventManager.callEvents(new VkWallReplyRestoreEvent(groupId, message));
    }

    @Override
    public void wallReplyDelete(Integer groupId, CallbackWallCommentDelete message) {
        eventManager.callEvents(new VkWallReplyDeleteEvent(groupId, message));
    }

    @Override
    public void boardPostNew(Integer groupId, TopicComment message) {
        eventManager.callEvents(new VkBoardPostNewEvent(groupId, message));
    }

    @Override
    public void boardPostEdit(Integer groupId, TopicComment message) {
        eventManager.callEvents(new VkBoardPostEditEvent(groupId, message));
    }

    @Override
    public void boardPostRestore(Integer groupId, TopicComment message) {
        eventManager.callEvents(new VkBoardPostRestoreEvent(groupId, message));
    }

    @Override
    public void boardPostDelete(Integer groupId, CallbackBoardPostDelete message) {
        eventManager.callEvents(new VkBoardPostDeleteEvent(groupId, message));
    }

    @Override
    public void marketCommentNew(Integer groupId, CallbackMarketComment message) {
        eventManager.callEvents(new VkMarketCommentNewEvent(groupId, message));
    }

    @Override
    public void marketCommentEdit(Integer groupId, CallbackMarketComment message) {
        eventManager.callEvents(new VkMarketCommentEditEvent(groupId, message));
    }

    @Override
    public void marketCommentRestore(Integer groupId, CallbackMarketComment message) {
        eventManager.callEvents(new VkMarketCommentRestoreEvent(groupId, message));
    }

    @Override
    public void marketCommentDelete(Integer groupId, CallbackMarketCommentDelete message) {
        eventManager.callEvents(new VkMarketCommentDeleteEvent(groupId, message));
    }

    @Override
    public void groupLeave(Integer groupId, CallbackGroupLeave message) {
        eventManager.callEvents(new VkGroupLeaveEvent(groupId, message));
    }

    @Override
    public void groupJoin(Integer groupId, CallbackGroupJoin message) {
        eventManager.callEvents(new VkGroupJoinEvent(groupId, message));
    }

    @Override
    public void groupChangeSettings(Integer groupId, CallbackGroupChangeSettings message) {
        eventManager.callEvents(new VkGroupChangeSettingsEvent(groupId, message));
    }

    @Override
    public void groupChangePhoto(Integer groupId, CallbackGroupChangePhoto message) {
        eventManager.callEvents(new VkGroupChangePhotoEvent(groupId, message));
    }

    @Override
    public void groupOfficersEdit(Integer groupId, CallbackGroupOfficersEdit message) {
        eventManager.callEvents(new VkGroupOfficersEditEvent(groupId, message));
    }

    @Override
    public void pollVoteNew(Integer groupId, CallbackPollVoteNew message) {
        eventManager.callEvents(new VkPollVoteNewEvent(groupId, message));
    }
}
