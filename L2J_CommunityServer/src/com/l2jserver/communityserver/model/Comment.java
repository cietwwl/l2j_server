/*
 * Copyright (C) 2004-2014 L2J Server
 * 
 * This file is part of L2J Server.
 * 
 * L2J Server is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * L2J Server is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jserver.communityserver.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Logger;

import com.l2jserver.communityserver.L2DatabaseFactory;
import com.l2jserver.communityserver.model.Topic.ConstructorType;

public class Comment
{
	private static final Logger _log = Logger.getLogger(Comment.class.getName());
	
	// SQL
	private static final String INSERT_COMMENT = "INSERT INTO comments (serverId, comment_id,comment_ownerid,comment_date,comment_post_id,comment_topic_id,comment_forum_id,comment_txt) values (?,?,?,?,?,?,?,?)";
	private static final String DELETE_COMMENT = "DELETE FROM comments WHERE serverId=? AND comment_forum_id=? AND comment_topic_id=? AND comment_Post_id=? AND comment_id=?";
	
	private final int _sqlDPId;
	private final int _commentId;
	private final int _commentOwnerId;
	private final long _commentDate;
	private final int _commentPostId;
	private final int _commentTopicId;
	private final int _commentForumId;
	private final String _commentTxt;
	
	/**
	 * @param ct
	 * @param sqlDPId
	 * @param commentId
	 * @param commentOwnerID
	 * @param date
	 * @param pid
	 * @param tid
	 * @param commentForumID
	 * @param txt
	 */
	public Comment(ConstructorType ct, final int sqlDPId, int commentId, int commentOwnerID, long date, int pid, int tid, int commentForumID, String txt)
	{
		_sqlDPId = sqlDPId;
		_commentId = commentId;
		_commentOwnerId = commentOwnerID;
		_commentDate = date;
		_commentPostId = pid;
		_commentTopicId = tid;
		_commentForumId = commentForumID;
		_commentTxt = txt;
		if (ct == ConstructorType.CREATE)
		{
			insertindb();
		}
	}
	
	public void insertindb()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(INSERT_COMMENT))
		{
			statement.setInt(1, _sqlDPId);
			statement.setInt(2, _commentId);
			statement.setInt(3, _commentOwnerId);
			statement.setLong(4, _commentDate);
			statement.setInt(5, _commentPostId);
			statement.setInt(6, _commentTopicId);
			statement.setInt(7, _commentForumId);
			statement.setString(8, _commentTxt);
			statement.execute();
		}
		catch (Exception e)
		{
			_log.warning("error while saving new Post to db " + e);
		}
	}
	
	public void deleteme()
	{
		try (Connection con = L2DatabaseFactory.getInstance().getConnection();
			PreparedStatement statement = con.prepareStatement(DELETE_COMMENT))
		{
			statement.setInt(1, _sqlDPId);
			statement.setInt(2, _commentForumId);
			statement.setInt(3, _commentTopicId);
			statement.setInt(4, _commentPostId);
			statement.setInt(5, _commentId);
			statement.execute();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 */
	public int getID()
	{
		return _commentId;
	}
	
	public String getText()
	{
		return _commentTxt;
	}
	
	public int getOwnerId()
	{
		return _commentOwnerId;
	}
	
	public Long getDate()
	{
		return _commentDate;
	}
}
