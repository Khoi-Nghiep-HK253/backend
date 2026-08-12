package com.hcmut.divvy.service;

import com.hcmut.divvy.dto.response.MediaAttachmentResponse;
import com.hcmut.divvy.service.model.DeleteMediaModel;
import com.hcmut.divvy.service.model.GetAttachmentsModel;
import com.hcmut.divvy.service.model.SelectMediaModel;
import com.hcmut.divvy.service.model.UploadMediaModel;

import java.util.List;

public interface MediaAttachmentService {

    /**
     * Uploads a file to Cloudinary and persists an attachment record linked to the
     * given entity.
     *
     * @param model contains the file, entity type/id, and the uploader's username
     * @return the saved attachment metadata as a response DTO
     */
    MediaAttachmentResponse upload(UploadMediaModel model);

    /**
     * Returns all attachments associated with the given entity.
     *
     * @param model contains entity type and entity id
     * @return list of attachment response DTOs
     */
    List<MediaAttachmentResponse> getAttachments(GetAttachmentsModel model);

    /**
     * Deletes an attachment by its ID, removing the file from Cloudinary and the DB
     * record.
     *
     * @param model contains the attachment id and the requester's username (for
     *              ownership check)
     */
    void delete(DeleteMediaModel model);

    /**
     * Selects a previously uploaded media attachment to make it active for an
     * entity.
     * Creates a new attachment record referencing the same file/public_id with the
     * current timestamp.
     *
     * @param model contains the attachment id and the requester's username
     * @return the newly saved active attachment response
     */
    MediaAttachmentResponse selectMedia(SelectMediaModel model);
}
